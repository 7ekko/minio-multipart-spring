package top.mind.miniomultipartspringstarter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.mind.miniomultipartspringstarter.domain.SysFileRecord;

import java.util.List;

/**
 * 文件上传记录Mapper接口
 *
 * @author
 * @date 2022-11-15
 */
@Mapper
public interface SysFileRecordMapper extends BaseMapper<SysFileRecord>{
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
     * 删除文件上传记录
     *
     * @param id 文件上传记录主键
     * @return 结果
     */
    public int deleteSysFileRecordById(Long id);

    /**
     * 批量删除文件上传记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysFileRecordByIds(Long[] ids);

    /**
     * 查询上传成功的文件记录
     *
     * @param fileMd5
     * @return
     */
    SysFileRecord selectByMd5(String fileMd5);

    /**
     * 更新上传文件记录（上传结果）
     *
     * @param uploadId
     * @param isUploaded
     * @param completedPartCount
     * @param createBy
     * @return
     */
    int updateSysFileRecordByUploadId(@Param("uploadId") String uploadId, @Param("isUploaded") int isUploaded, @Param("completedPartCount") Integer completedPartCount, @Param("createBy") String createBy);
}
