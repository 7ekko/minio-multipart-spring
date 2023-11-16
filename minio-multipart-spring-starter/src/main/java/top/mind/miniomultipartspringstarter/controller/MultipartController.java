package top.mind.miniomultipartspringstarter.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.mind.miniomultipartspringstarter.template.MinioTemplate;
import top.mind.miniomultipartspringstarter.config.RestResponse;
import top.mind.miniomultipartspringstarter.domain.bo.MultipartInitBO;
import top.mind.miniomultipartspringstarter.domain.param.MultipartCompleteParam;
import top.mind.miniomultipartspringstarter.domain.param.MultipartInitParam;
import javax.annotation.Resource;
import java.util.Objects;

/**
 * 流程  init -> chunkUpload -> complete
 * <p>
 * 已上传过的文件秒传（文件名+文件大小+bucket   是一样的表示同一个文件）
 * <p>
 * 24小时内可以断点续传（生成的chunkUpload有效期是24小时，redis中 MULTIPART_UPLOADID_KEY和MULTIPART_UPLOADID_CHUNK_KEY有效期是24小时）
 * 超过24小时均失效
 * <p>
 * 所有的chunkUpload调用成功后，调用complete； chunkUpload未都调用成功时调用complete会返回500
 */
@RestController
@RequestMapping("/api/file/multipart")
public class MultipartController {
    @Resource
    MinioTemplate minioTemplate;

    @PostMapping("/init")
    public RestResponse initMultiPartUpload(@RequestBody MultipartInitParam requestParam) {
        final MultipartInitBO multipartInitBO = minioTemplate.initMultiPartUpload(requestParam);
        if (Objects.nonNull(multipartInitBO)) {
            return RestResponse.ok(multipartInitBO);
        } else {
            return RestResponse.fail("初始化分片失败");
        }
    }

    @PostMapping("/chunkUpload")
    public RestResponse chunkUpload(@RequestPart("file") MultipartFile file, @RequestParam("uploadId") String uploadId, @RequestParam("chunk") String chunk) {
        String msg = minioTemplate.chunkUpload(file, uploadId, chunk);
        return msg == null ? RestResponse.ok() : RestResponse.fail(msg);
    }

    @PutMapping("/complete")
    public RestResponse completeMultiPartUpload(@RequestBody MultipartCompleteParam param) {
        String fileAddress = minioTemplate.mergeMultipartUpload(param);
        return fileAddress == null ? RestResponse.fail("合并失败请重新上传") : RestResponse.ok(fileAddress);
    }
}
