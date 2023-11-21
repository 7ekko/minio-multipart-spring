package top.mind.miniomultipartspringstarter.controller;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import top.mind.miniomultipartspringstarter.config.RestResponse;
import top.mind.miniomultipartspringstarter.domain.SysFileRecord;
import top.mind.miniomultipartspringstarter.domain.param.RecycleOrRecoveryVM;
import top.mind.miniomultipartspringstarter.service.ISysFileRecordService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @Resource
    private ISysFileRecordService sysFileRecordService;
    @Autowired
    @Qualifier("getMinioClient")
    private MinioClient client;


    // 加载上传的文件
    @RequestMapping(value = "/load", method = RequestMethod.POST)
    public RestResponse load() {
        List<SysFileRecord> list = sysFileRecordService.load();
        // isUploaded == 0 的放在后面
        list.sort((o1, o2) -> {
            if (o1.getIsUploaded() == 0 && o2.getIsUploaded() == 1) {
                return 1;
            } else if (o1.getIsUploaded() == 1 && o2.getIsUploaded() == 0) {
                return -1;
            } else {
                return 0;
            }
        });
        return RestResponse.ok(list);
    }
    // 回收站 列表
    @RequestMapping(value = "/recycle", method = RequestMethod.POST)
    public RestResponse recycle() {
        List<SysFileRecord> list = sysFileRecordService.recycle();
        return RestResponse.ok(list);
    }

    // 移到回收站 或者 恢复
    @RequestMapping(value = "/recycleOrRecovery", method = RequestMethod.POST)
    public RestResponse recycle(@RequestBody @Valid RecycleOrRecoveryVM vm) {
         sysFileRecordService.recycleOrRecovery(vm);
        return RestResponse.ok();
    }



}
