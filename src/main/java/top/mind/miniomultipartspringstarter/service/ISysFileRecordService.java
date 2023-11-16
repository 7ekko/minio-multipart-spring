package top.mind.miniomultipartspringstarter.service;



import top.mind.miniomultipartspringstarter.domain.SysFileRecord;

import java.util.List;

/**
 * 文件上传记录Service接口
 */
public interface ISysFileRecordService {
    /**
     * 查询文件上传记录
     *
     * @param id 文件上传记录主键
     * @return 文件上传记录
     */
    public SysFileRecord selectSysFileRecordById(Long id);

    /**
     * 查询文件上传记录列表
     *
     * @param sysFileRecord 文件上传记录
     * @return 文件上传记录集合
     */
    public List<SysFileRecord> selectSysFileRecordList(SysFileRecord sysFileRecord);

    /**
     * 新增文件上传记录
     *
     * @param sysFileRecord 文件上传记录
     * @return 结果
     */
    public int insertSysFileRecord(SysFileRecord sysFileRecord);

    /**
     * 修改文件上传记录
     *
     * @param sysFileRecord 文件上传记录
     * @return 结果
     */
    public int updateSysFileRecord(SysFileRecord sysFileRecord);

    /**
     * 批量删除文件上传记录
     *
     * @param ids 需要删除的文件上传记录主键集合
     * @return 结果
     */
    public int deleteSysFileRecordByIds(Long[] ids);

    /**
     * 删除文件上传记录信息
     *
     * @param id 文件上传记录主键
     * @return 结果
     */
    public int deleteSysFileRecordById(Long id);

    /**
     * 查询上传的文件记录
     *
     * @param fileMd5
     * @return
     */
    SysFileRecord selectByMd5(String fileMd5);

    /**
     * 更新文件上传合并chunk的结果
     *
     * @param uploadId
     * @param completedPartCount
     * @param isUploaded
     * @return
     */
    int updateByUploadId(String uploadId, Integer completedPartCount, int isUploaded);
}
