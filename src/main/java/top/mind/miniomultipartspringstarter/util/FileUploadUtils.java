package top.mind.miniomultipartspringstarter.util;

import cn.hutool.core.util.IdUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import top.mind.miniomultipartspringstarter.domain.FileTypeEnum;

import java.util.Objects;

/**
 * 文件上传工具类
 *
 * @author
 */
public class FileUploadUtils {

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        String fileSuffix = FilenameUtils.getExtension(originFileName);

        String type = FileTypeEnum.getBySuffix(fileSuffix).getCode();
        return StringUtils.format("/{}/{}_{}.{}", type, FilenameUtils.getBaseName(IdUtil.fastUUID()),
                Seq.getId(Seq.uploadSeqType), fileSuffix);
    }
    /**
     * 编码文件名
     */
    public static final String extractFilename(String originFileName) {
        String extension = FilenameUtils.getExtension(originFileName);

        return extractFilename(originFileName, extension);
    }
    public static final String extractFilename(String originFileName, String fileSuffix) {
        String type = FileTypeEnum.getBySuffix(fileSuffix).getCode();
        return StringUtils.format("/{}/{}_{}.{}", type, FilenameUtils.getBaseName(IdUtil.fastUUID()),
                Seq.getId(Seq.uploadSeqType), FilenameUtils.getExtension(originFileName));
    }


}
