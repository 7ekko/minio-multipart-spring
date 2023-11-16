package top.mind.miniomultipartspringstarter.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 分片上传合并分片
 */
@Data
public class MultipartCompleteParam {
    @NotEmpty(message = "上传id不能为空")
    private String uploadId;
}
