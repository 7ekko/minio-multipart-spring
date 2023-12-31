package top.mind.miniomultipartspringstarter.util;

/**
 * 缓存的key 常量
 *
 * @author
 */
public class CacheConstants {

    /**
     * 分片上传
     * multipart_uploadId:uploadId
     */
    public final static String MULTIPART_UPLOADID_KEY = "multipart_uploadId:%s";
    /**
     * 分片上传chunk
     * multipart_uploadId:uploadId:chunk
     */
    public final static String MULTIPART_UPLOADID_CHUNK_KEY = "multipart_uploadId:%s:%s";
    /**
     * 回收站
     * file:recycle:bucket:fileName
     */
    public final static String FILE_RECYCLE_KEY = "file:recycle:%s:%s";
}
