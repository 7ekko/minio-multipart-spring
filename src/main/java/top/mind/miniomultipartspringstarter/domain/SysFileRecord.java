package top.mind.miniomultipartspringstarter.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 文件上传记录对象 sys_file_record
 */
@TableName("sys_file_record")
@Data
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

    @TableField(exist = false)
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 备注
     */
    private String remark;

    private Integer isRecycle;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(String fileUrls) {
        this.fileUrls = fileUrls;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(Integer isUploaded) {
        this.isUploaded = isUploaded;
    }

    public Long getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Long totalChunks) {
        this.totalChunks = totalChunks;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Integer getCompletedParts() {
        return completedParts;
    }

    public void setCompletedParts(Integer completedParts) {
        this.completedParts = completedParts;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsRecycle() {
        return isRecycle;
    }

    public void setIsRecycle(Integer isRecycle) {
        this.isRecycle = isRecycle;
    }
}
