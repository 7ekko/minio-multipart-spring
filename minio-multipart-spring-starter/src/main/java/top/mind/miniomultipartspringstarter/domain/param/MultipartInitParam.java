package top.mind.miniomultipartspringstarter.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 分片上传初始化
 */
@Data
public class MultipartInitParam {
    @NotEmpty(message = "文件名不能为空")
    private String filename;
    private String contentType;
    @NotNull(message = "文件大小不能为空")
    private Long size;
    private String md5;
}
