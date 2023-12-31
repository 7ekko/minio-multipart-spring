package top.mind.miniomultipartspringstarter.service;

import io.minio.StatObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;

/**
 * 文件上传接口
 *
 * @author konglingdi
 */
public interface MinioSysFileService {
    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception;

    /**
     * 下载文件
     *
     * @param fileName
     * @param outputStream
     * @throws Exception
     */
    public void downloadFile(String fileName, ServletOutputStream outputStream) throws Exception;


    /**
     * 获取文件信息
     *
     * @param fileName
     */
    StatObjectResponse metedataSingleFile(String fileName);

    void removeObject(String bucketName, String objectName);
}
