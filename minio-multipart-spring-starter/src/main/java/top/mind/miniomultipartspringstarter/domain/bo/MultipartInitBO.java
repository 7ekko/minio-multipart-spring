package top.mind.miniomultipartspringstarter.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class MultipartInitBO {
    /**
     * 分片上传id
     */
    private String uploadId;
    /**
     * 分片上传url集合
     */
    private List<String> uploadUrlList;
    /**
     * 分片数量
     */
    private Long partCount;
    /**
     * 文件url(秒传)
     */
    private String url;
}
