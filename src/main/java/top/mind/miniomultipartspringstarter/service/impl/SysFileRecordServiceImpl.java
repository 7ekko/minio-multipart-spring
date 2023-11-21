package top.mind.miniomultipartspringstarter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.mind.miniomultipartspringstarter.domain.FileTypeEnum;
import top.mind.miniomultipartspringstarter.domain.SysFileRecord;
import top.mind.miniomultipartspringstarter.domain.param.RecycleOrRecoveryVM;
import top.mind.miniomultipartspringstarter.mapper.SysFileRecordMapper;
import top.mind.miniomultipartspringstarter.service.ISysFileRecordService;
import top.mind.miniomultipartspringstarter.service.MinioSysFileService;
import top.mind.miniomultipartspringstarter.template.MinioTemplate;
import top.mind.miniomultipartspringstarter.util.CacheConstants;
import top.mind.miniomultipartspringstarter.util.RedisCache;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 文件上传记录Service业务层处理
 */
@Service
public class SysFileRecordServiceImpl implements ISysFileRecordService {
    @Resource
    private SysFileRecordMapper sysFileRecordMapper;

    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.url}")
    private String url;
    @Resource
    private RedisCache redisCache;
    @Resource
    private MinioSysFileService minioSysFileService;


    /**
     * 查询文件上传记录
     */
    @Override
    public SysFileRecord selectSysFileRecordById(Long id) {
        return sysFileRecordMapper.selectSysFileRecordById(id);
    }

    /**
     * 查询文件上传记录列表
     */
    @Override
    public List<SysFileRecord> selectSysFileRecordList(SysFileRecord sysFileRecord) {
        return sysFileRecordMapper.selectSysFileRecordList(sysFileRecord);
    }

    /**
     * 新增文件上传记录
     */
    @Override
    public int insertSysFileRecord(SysFileRecord sysFileRecord) {
        // TODO 创建人
//        sysFileRecord.setCreateBy("");
        sysFileRecord.setCreateTime(new Date());
        return sysFileRecordMapper.insertSysFileRecord(sysFileRecord);
    }

    /**
     * 修改文件上传记录
     */
    @Override
    public int updateSysFileRecord(SysFileRecord sysFileRecord) {
        sysFileRecord.setUpdateTime(new Date());
        return sysFileRecordMapper.updateSysFileRecord(sysFileRecord);
    }

    /**
     * 批量删除文件上传记录
     */
    @Override
    public int deleteSysFileRecordByIds(Long[] ids) {
        return sysFileRecordMapper.deleteSysFileRecordByIds(ids);
    }

    /**
     * 删除文件上传记录信息
     *
     * @param id 文件上传记录主键
     * @return 结果
     */
    @Override
    public int deleteSysFileRecordById(Long id) {
        return sysFileRecordMapper.deleteSysFileRecordById(id);
    }


    @Override
    public SysFileRecord selectByMd5(String fileMd5) {
        return sysFileRecordMapper.selectByMd5(fileMd5);
    }

    @Override
    public int updateByUploadId(String uploadId, Integer completedPartCount, int isUploaded) {
        // TODO
        String updateBy = "";
        return sysFileRecordMapper.updateSysFileRecordByUploadId(uploadId, isUploaded, completedPartCount, updateBy);
    }

    @Override
    public List<SysFileRecord> load() {
        List<SysFileRecord> sysFileRecords = sysFileRecordMapper.selectList(
                new QueryWrapper<SysFileRecord>()
                        .eq("is_recycle", 0));

        for (SysFileRecord s : sysFileRecords) {
            Long totalChunks = s.getTotalChunks();
            if (s.getIsUploaded() != 1) {
                AtomicReference<Long> completedPartCount = new AtomicReference<>(0L);
                ArrayList<String> list = new ArrayList<>();
                Map<String, String> chunkKeyToUploadUrlMap = new HashMap<>();
                for (Long i = 0L; i < totalChunks; i++) {
                    String chunkKey = String.format(CacheConstants.MULTIPART_UPLOADID_CHUNK_KEY, s.getUploadId(), "chunk_" + i);
                    list.add(chunkKey);
                }

                Map<String, String> cacheObjects = redisCache.<String>getCacheObjects(list);
                cacheObjects.values().forEach(value -> {
                    if ("1".equals(value)) {
                        completedPartCount.getAndSet(completedPartCount.get() + 1);
                    }
                });

                s.setTotalChunks(totalChunks);
                s.setCompletedParts(Math.toIntExact(completedPartCount.get()));
            }
            s.setFileType(
                    FileTypeEnum.getBySuffix(
                            FilenameUtils.getExtension(s.getFileName())).getCode());
            s.setFileUrl(url + "/" + bucketName + "/" + s.getFileUrl());
        }
        return sysFileRecords;

    }

    @Override
    public SysFileRecord selectSysFileRecordByFileMd5(String md5) {
        return sysFileRecordMapper.selectOne(new QueryWrapper<SysFileRecord>().eq("md5", md5));
    }

    @Override
    public List<SysFileRecord> recycle() {
        List<SysFileRecord> sysFileRecords = sysFileRecordMapper.selectList(new QueryWrapper<SysFileRecord>().eq("is_recycle", 1));


        List<SysFileRecord> list = sysFileRecords.stream().map(s -> {
            String recycleKey = String.format(CacheConstants.FILE_RECYCLE_KEY, bucketName, s.getFileName());
            if (!redisCache.hasKey(recycleKey)) {
                sysFileRecordMapper.deleteSysFileRecordById(s.getId());
                // 异步删除 minio 文件
                minioSysFileService.removeObject(bucketName, s.getFileName());
                return null;
            }else {
                return s;
            }
        }).collect(Collectors.toList());

       return list.stream().filter(Objects::nonNull).collect(Collectors.toList());

    }

    @Override
    public void recycleOrRecovery(RecycleOrRecoveryVM vm) {
        String fileName = vm.getFileName();
        Integer isRecycle = vm.getIsRecycle();
        SysFileRecord file = sysFileRecordMapper.selectOne(new QueryWrapper<SysFileRecord>().eq("file_name", fileName));
        file.setIsRecycle(
                isRecycle == 0 ? 1 : 0
        );
        sysFileRecordMapper.updateById(file);
        if (isRecycle == 0){
            // 删除操作
            // redis 新增key
            String fileRecycleKey = String.format(CacheConstants.FILE_RECYCLE_KEY, bucketName, fileName);
            redisCache.setCacheObject(fileRecycleKey, "1", 7, TimeUnit.DAYS);
        }else {
            // 恢复操作
            // redis 删除key
            String key = String.format(CacheConstants.FILE_RECYCLE_KEY, bucketName, fileName);
            redisCache.deleteObject(key);
        }
    }




    public static void main(String[] args) {
        String tyuexam = String.format(CacheConstants.FILE_RECYCLE_KEY, "tyuexam", "ClashX.dmg");

        System.out.println(tyuexam);
    }

}
