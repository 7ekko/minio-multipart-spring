package top.mind.miniomultipartspringstarter.service.impl;


import org.springframework.stereotype.Service;
import top.mind.miniomultipartspringstarter.domain.SysFileRecord;
import top.mind.miniomultipartspringstarter.mapper.SysFileRecordMapper;
import top.mind.miniomultipartspringstarter.service.ISysFileRecordService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 文件上传记录Service业务层处理
 *
 * @author ruoyi
 * @date 2022-11-15
 */
@Service
public class SysFileRecordServiceImpl implements ISysFileRecordService {
    @Resource
    private SysFileRecordMapper sysFileRecordMapper;

    /**
     * 查询文件上传记录
     *
     * @param id 文件上传记录主键
     * @return 文件上传记录
     */
    @Override
    public SysFileRecord selectSysFileRecordById(Long id) {
        return sysFileRecordMapper.selectSysFileRecordById(id);
    }

    /**
     * 查询文件上传记录列表
     *
     * @param sysFileRecord 文件上传记录
     * @return 文件上传记录
     */
    @Override
    public List<SysFileRecord> selectSysFileRecordList(SysFileRecord sysFileRecord) {
        return sysFileRecordMapper.selectSysFileRecordList(sysFileRecord);
    }

    /**
     * 新增文件上传记录
     *
     * @param sysFileRecord 文件上传记录
     * @return 结果
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
     *
     * @param sysFileRecord 文件上传记录
     * @return 结果
     */
    @Override
    public int updateSysFileRecord(SysFileRecord sysFileRecord) {
        sysFileRecord.setUpdateTime(new Date());
        return sysFileRecordMapper.updateSysFileRecord(sysFileRecord);
    }

    /**
     * 批量删除文件上传记录
     *
     * @param ids 需要删除的文件上传记录主键
     * @return 结果
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
}
