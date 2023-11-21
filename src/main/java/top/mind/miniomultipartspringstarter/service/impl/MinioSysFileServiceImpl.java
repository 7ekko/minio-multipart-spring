package top.mind.miniomultipartspringstarter.service.impl;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.mind.miniomultipartspringstarter.config.MinioConfig;
import top.mind.miniomultipartspringstarter.service.MinioSysFileService;
import top.mind.miniomultipartspringstarter.util.FileUploadUtils;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;


/**
 * Minio 文件存储
 *
 * @author konglingdi
 */
@Slf4j
// @Primary
@Service
public class MinioSysFileServiceImpl implements MinioSysFileService {
    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    @Qualifier("getMinioClient")
    private MinioClient client;

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = FileUploadUtils.extractFilename(file);
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();
        client.putObject(args);
        return minioConfig.getUrl() + "/" + minioConfig.getBucketName() + "/" + fileName;
//        return fileName;
    }

    @Override
    public void downloadFile(String fileName, ServletOutputStream outputStream) throws Exception {
        try (InputStream inputStream = client.getObject(GetObjectArgs
                .builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .build());
        ) {

            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("文件下载异常");
            e.printStackTrace();
        }
    }

    @Override
    public StatObjectResponse metedataSingleFile(String fileName) {
        try {
            StatObjectResponse statObjectResponse = client.statObject(StatObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .build());
            return statObjectResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void removeObject(String bucketName, String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("删除文件失败，{}", e.getMessage());
        }
    }

}
