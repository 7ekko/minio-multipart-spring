package top.mind.miniomultipartspringstarter.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum FileTypeEnum {

    IMAGE("image", "图片", Arrays.asList("jpg", "jpeg", "png", "gif")),
    VIDEO("video", "视频", Arrays.asList("mp4", "avi", "mkv")),
    AUDIO("audio", "音频", Arrays.asList("mp3", "wav", "flac")),
    DOC("doc", "文档", Arrays.asList("doc", "docx", "pdf", "txt")),
    OTHER("other", "其他", Collections.singletonList("other"));

    private String code;
    private String name;
    private List<String> suffix;

    FileTypeEnum(String code, String name, List<String> suffix) {
        this.code = code;
        this.name = name;
        this.suffix = suffix;
    }

    public static FileTypeEnum getBySuffix(String suffix) {
        for (FileTypeEnum value : FileTypeEnum.values()) {
            if (value.getSuffix().contains(suffix.toLowerCase())) {
                return value;
            }
        }
        return OTHER;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<String> getSuffix() {
        return suffix;
    }
}
