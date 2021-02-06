package com.zf1976.ant.common.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.Result;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.common.log.pojo.vo.base.AbstractLogVO;
import com.zf1976.ant.common.log.query.LogQueryParam;
import com.zf1976.ant.common.log.service.SysLogService;
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
    public Result<IPage<AbstractLogVO>> selectLogPage(@RequestBody RequestPage<LogQueryParam> requestPage) {
        return Result.success(service.selectLogPage(requestPage));
    }

    @PostMapping("/users/page")
    public Result<IPage<AbstractLogVO>> selectUserLogPage(@RequestBody RequestPage<LogQueryParam> requestPage) {
        return Result.success(service.selectUserLogPage(requestPage));
    }

    @DeleteMapping("/delete")
    public Result<Optional<Void>> deleteLog(@RequestBody Set<Long> ids) {
        return Result.success(service.deleteLog(ids));
    }

    @DeleteMapping("/delete/error")
    public Result<Optional<Void>> deleteErrorLog() {
        return Result.success(service.deleteErrorLog());
    }

    @DeleteMapping("/delete/info")
    public Result<Optional<Void>> deleteInfoLog() {
        return Result.success(service.deleteInfoLog());
    }
}
