package top.mind.miniomultipartspringstarter.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashMultimap;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListPartsResponse;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.mind.miniomultipartspringstarter.config.CustomMinioClient;
import top.mind.miniomultipartspringstarter.config.MinioConfig;
import top.mind.miniomultipartspringstarter.domain.SysFileRecord;
import top.mind.miniomultipartspringstarter.domain.bo.MultipartInitBO;
import top.mind.miniomultipartspringstarter.domain.param.MultipartCompleteParam;
import top.mind.miniomultipartspringstarter.domain.param.MultipartInitParam;
import top.mind.miniomultipartspringstarter.service.ISysFileRecordService;
import top.mind.miniomultipartspringstarter.util.CacheConstants;
import top.mind.miniomultipartspringstarter.util.FileUploadUtils;
import top.mind.miniomultipartspringstarter.util.RedisCache;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MinioTemplate {
    @Autowired
    private CustomMinioClient customMinioClient;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private ISysFileRecordService fileRecordService;

    private static final String CHUNK_UPLOAD_PREFIX = "/api/file/multipart/chunkUpload";

    public MultipartInitBO initMultiPartUpload(MultipartInitParam requestParam) {

        // 文件大小
        long size = requestParam.getSize();
        // 分片数量
        long partCount = size % minioConfig.getPartSize() != 0 ? size / minioConfig.getPartSize() + 1 : size / minioConfig.getPartSize();

        SysFileRecord fileRecord = fileRecordService.selectByMd5(requestParam.getMd5());
        if (null != fileRecord) {
            Integer isUploaded = fileRecord.getIsUploaded();
            if (1 == isUploaded) {
                MultipartInitBO multipartInitBO = new MultipartInitBO();
                // 已经上传过，秒传
                multipartInitBO.setUrl(fileRecord.getFileUrl());
                return multipartInitBO;
            }

            // redis中缓存默认是24小时失效，url链接24小时失效
            // 在24小时之内再次分片上传都可使用之前分片上传但没都上传成功的分片文件。
            // 考虑上传需要耗时，new Date() - createTime < 20小时才使用之前的结果，否则重新分片初始化
            Date now = new Date();
            Date createTime = fileRecord.getCreateTime();
            long hour = DateUtil.between(createTime, now, DateUnit.HOUR);
            //  20小时
            if (hour < 20) {
                List<String> partList = new ArrayList<>();
                for (int i = 1; i <= fileRecord.getTotalChunks(); i++) {
                    String chunk = "chunk_" + (i - 1);
                    partList.add(CHUNK_UPLOAD_PREFIX + "?uploadId=" + fileRecord.getUploadId() + "&chunk=" + chunk);
                }
                MultipartInitBO multipartInitBO = new MultipartInitBO();
                multipartInitBO.setUploadId(fileRecord.getUploadId());
                multipartInitBO.setPartCount(fileRecord.getTotalChunks());
                multipartInitBO.setUploadUrlList(partList);
                return multipartInitBO;
            }

        }

        return multipartUpload(requestParam, partCount, requestParam.getMd5());
    }

    /**
     * 初始化分片上传
     *
     * @param requestParam
     * @param partCount
     * @return
     */
    private MultipartInitBO multipartUpload(MultipartInitParam requestParam, long partCount, String fileMd5) {

        try {
            if (StrUtil.isBlank(requestParam.getContentType())) {
                requestParam.setContentType("application/octet-stream");
            }

            String fileNewName = FileUploadUtils.extractFilename(requestParam.getFilename());

            HashMultimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", requestParam.getContentType());
            String uploadId = customMinioClient.initMultiPartUpload(minioConfig.getBucketName(), null, fileNewName, headers, null);

            // 存缓存 uploadId : filePath
            String redisKey = String.format(CacheConstants.MULTIPART_UPLOADID_KEY, uploadId);
            // TODO 设置分片上传 uploadId缓存1天
            redisCache.setCacheObject(redisKey, fileNewName, 1, TimeUnit.DAYS);

            Map<String, String> reqParams = new HashMap<>();
            Map<String, String> chunkMap = new HashMap<>();
            // 给前端
            List<String> partList = new ArrayList<>();
            reqParams.put("uploadId", uploadId);
            for (int i = 1; i <= partCount; i++) {
                reqParams.put("partNumber", String.valueOf(i));
                String uploadUrl = customMinioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(minioConfig.getBucketName())
                                .object(fileNewName)
                                .expiry(1, TimeUnit.DAYS)
                                .extraQueryParams(reqParams)
                                .build());
                String chunk = "chunk_" + (i - 1);
                chunkMap.put(chunk, uploadUrl);
                partList.add(CHUNK_UPLOAD_PREFIX + "?uploadId=" + uploadId + "&chunk=" + chunk);

                String chunkKey = String.format(CacheConstants.MULTIPART_UPLOADID_CHUNK_KEY, uploadId, chunk);
                redisCache.setCacheObject(chunkKey, uploadUrl, 1, TimeUnit.DAYS);
            }

            // 新增分片上传记录数据
            SysFileRecord newFileRecord = new SysFileRecord();
            newFileRecord.setMd5(fileMd5);
            newFileRecord.setBucket(minioConfig.getBucketName());
            newFileRecord.setSize(requestParam.getSize());
            newFileRecord.setTotalChunks(partCount);
            newFileRecord.setFileName(requestParam.getFilename());
            newFileRecord.setFileUrl(fileNewName);
            newFileRecord.setFileUrls(JSON.toJSONString(chunkMap));
            newFileRecord.setUploadId(uploadId);
            fileRecordService.insertSysFileRecord(newFileRecord);

            MultipartInitBO multipartInitBO = new MultipartInitBO();
            multipartInitBO.setPartCount(partCount);
            multipartInitBO.setUploadId(uploadId);
            multipartInitBO.setUploadUrlList(partList);
            return multipartInitBO;
        } catch (Exception e) {
            log.error("分片初始化失败，{}", e.getMessage());
            return null;
        }
    }

    public String chunkUpload(MultipartFile file, String uploadId, String chunk) {
        String chunkKey = String.format(CacheConstants.MULTIPART_UPLOADID_CHUNK_KEY, uploadId, chunk);
        String uploadUrl = redisCache.<String>getCacheObject(chunkKey);
        if ("1".equals(uploadUrl)) {
            // 断点续传
            return null;
        }
        String msg = putMethod(uploadUrl, file);
        if (null == msg) {
            Long expire = redisCache.getExpire(chunkKey);
            // chunk已经上传成功
            redisCache.setCacheObject(chunkKey, "1", expire.intValue(), TimeUnit.SECONDS);
        }
        // 因并发问题不可直接更新chunk上传结果到记录表中 
        // 可使用其他解决方法 比如 利用数据库乐观锁/消息队列/回调接口
//        String result = redisCache.<String>getCacheObject(chunkKey);     
//        updateChunkResult(uploadId, chunk, result);
        return msg;
    }

    public String mergeMultipartUpload(MultipartCompleteParam param) {
        // true 表示合并chunk成功
        int isUploaded = 0;
        // 上传成功的chunk数量
        Integer completedPartCount = 0;
        try {
            String redisKey = String.format(CacheConstants.MULTIPART_UPLOADID_KEY, param.getUploadId());
            final String filePath = redisCache.getCacheObject(redisKey);
            if (StringUtils.isBlank(filePath)) {
                return null;
            }
            Integer maxPart = 1000;
            Part[] parts = new Part[maxPart];
            ListPartsResponse partResult = customMinioClient.listMultipart(minioConfig.getBucketName(), null, filePath, maxPart, 0, param.getUploadId(), null, null);
            int partNumber = 1;
            for (Part part : partResult.result().partList()) {
                parts[partNumber - 1] = new Part(partNumber, part.etag());
                partNumber++;
            }

            String chunkListKeyPattern = String.format(CacheConstants.MULTIPART_UPLOADID_CHUNK_KEY, param.getUploadId(), "chunk_*");
            Collection<String> chunkKeyList = redisCache.<String>keys(chunkListKeyPattern);
            if (CollectionUtil.isEmpty(chunkKeyList)) {
                // 缓存过期,不能合并
                log.info(">>>>>>>>>>>{}缓存过期，不能合并", param.getUploadId());
                return null;
            }
            int realyPartNumber = chunkKeyList.size();
            completedPartCount = partResult.result().partList().size();
            // 若上传的分片少于实际分片数量，不能合并
            if (completedPartCount < realyPartNumber) {
                // 分片文件不够，不能合并
                log.info(">>>>>>>>>>{}分片文件不够，不能合并", param.getUploadId());
                return null;
            }

            customMinioClient.mergeMultipartUpload(minioConfig.getBucketName(), null, filePath, param.getUploadId(), parts, null, null);
            // 合并成功，isUPloaded设为1，删除redis缓存
            isUploaded = 1;
            redisCache.deleteObject(redisKey);
            redisCache.deleteObject(chunkKeyList);
            // 返回文件的绝对路径
            return filePath;
        } catch (Exception e) {
            log.error("分片合并失败，{}", e.fillInStackTrace().toString());
            return null;
        } finally {
            log.info(">>>>>>>>>>>>合并chunk,uploadId:{}, 结果：{}", param.getUploadId(), isUploaded);
            // 更新上传文件记录
            fileRecordService.updateByUploadId(param.getUploadId(), completedPartCount, isUploaded);
        }

    }

    /**
     * 向minio上传分片文件
     *
     * @param url
     * @param file
     * @return
     */
    private String putMethod(String url, MultipartFile file) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            // RequestConfig requestConfig = RequestConfig.custom()
            //         .setSocketTimeout(2000) //服务器响应超时时间
            //         .setConnectTimeout(2000) //连接服务器超时时间
            //         .build();
            // httpPut.setConfig(requestConfig);

            // UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "utf-8");
            // httpPost.setEntity(entity);
            // httpPut.setHeader("Content-Type", "application/x-www-form-urlencoded");
            // 由客户端执行(发送)请求
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(file.getBytes());
            httpPut.setEntity(byteArrayEntity);
            response = httpClient.execute(httpPut);
            log.info(">>>>>>>>>>>>>>" + url + "的响应状态为:{}, resonPhrase: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() != 200) {
                return "上传失败";
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
