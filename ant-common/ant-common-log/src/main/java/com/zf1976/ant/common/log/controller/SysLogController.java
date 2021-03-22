package com.zf1976.ant.common.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.common.log.pojo.vo.base.AbstractLogVO;
import com.zf1976.ant.common.log.query.LogQueryParam;
import com.zf1976.ant.common.log.service.SysLogService;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2021/1/25
 **/
@RestController
@RequestMapping("/api/logs")
public class SysLogController {

    private final SysLogService service;

    public SysLogController(SysLogService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public ResultData<IPage<AbstractLogVO>> selectLogPage(@RequestBody RequestPage<LogQueryParam> requestPage) {
        return ResultData.success(service.selectLogPage(requestPage));
    }

    @PostMapping("/users/page")
    public ResultData<IPage<AbstractLogVO>> selectUserLogPage(@RequestBody RequestPage<LogQueryParam> requestPage) {
        return ResultData.success(service.selectUserLogPage(requestPage));
    }

    @DeleteMapping("/delete")
    public ResultData<Optional<Void>> deleteLog(@RequestBody Set<Long> ids) {
        return ResultData.success(service.deleteLog(ids));
    }

    @DeleteMapping("/delete/error")
    public ResultData<Optional<Void>> deleteErrorLog() {
        return ResultData.success(service.deleteErrorLog());
    }

    @DeleteMapping("/delete/info")
    public ResultData<Optional<Void>> deleteInfoLog() {
        return ResultData.success(service.deleteInfoLog());
    }
}
