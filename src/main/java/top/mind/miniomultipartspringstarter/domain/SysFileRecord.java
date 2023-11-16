package top.mind.miniomultipartspringstarter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 文件上传记录对象 sys_file_record
 *
 * @date 2022-11-15
 */
public class SysFileRecord {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 上传分片的链接
     */
    private String fileUrls;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * MD5
     */
    private String md5;

    /**
     * 上传id
     */
    private String uploadId;

    /**
     * 是否已上传
     */
    private Integer isUploaded;

    /**
     * 分片总块数
     */
    private Long totalChunks;

    /**
     * 文件大小（B）
     */
    private Long size;

    /**
     * 已完成片数
     */
    private Integer completedParts;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * minio桶名称
     */
    private String bucket;

    /**
     * 各分片上传的结果
     */
    private String chunkResult;

    /**
     * 版本
     **/
    private Long version;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setFileUrls(String fileUrls) {
        this.fileUrls = fileUrls;
    }

    public String getFileUrls() {
        return fileUrls;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setIsUploaded(Integer isUploaded) {
        this.isUploaded = isUploaded;
    }

    public Integer getIsUploaded() {
        return isUploaded;
    }

    public void setTotalChunks(Long totalChunks) {
        this.totalChunks = totalChunks;
    }

    public Long getTotalChunks() {
        return totalChunks;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getSize() {
        return size;
    }

    public void setCompletedParts(Integer completedParts) {
        this.completedParts = completedParts;
    }

    public Integer getCompletedParts() {
        return completedParts;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getChunkResult() {
        return chunkResult;
    }

    public void setChunkResult(String chunkResult) {
        this.chunkResult = chunkResult;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("fileUrls", getFileUrls())
                .append("fileName", getFileName())
                .append("md5", getMd5())
                .append("uploadId", getUploadId())
                .append("isUploaded", getIsUploaded())
                .append("totalChunks", getTotalChunks())
                .append("size", getSize())
                .append("completedParts", getCompletedParts())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
