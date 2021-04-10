package com.zf1976.ant.common.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.log.pojo.vo.base.AbstractLogVO;
import com.zf1976.ant.common.log.query.LogQueryParam;
import com.zf1976.ant.common.log.service.SysLogService;
import com.zf1976.ant.upms.biz.pojo.query.Query;
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
    public DataResult<IPage<AbstractLogVO>> selectLogPage(@RequestBody Query<LogQueryParam> requestPage) {
        return DataResult.success(service.selectLogPage(requestPage));
    }

    @PostMapping("/users/page")
    public DataResult<IPage<AbstractLogVO>> selectUserLogPage(@RequestBody Query<LogQueryParam> requestPage) {
        return DataResult.success(service.selectUserLogPage(requestPage));
    }

    @DeleteMapping("/delete")
    public DataResult<Optional<Void>> deleteLog(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteLog(ids));
    }

    @DeleteMapping("/delete/error")
    public DataResult<Optional<Void>> deleteErrorLog() {
        return DataResult.success(service.deleteErrorLog());
    }

    @DeleteMapping("/delete/info")
    public DataResult<Optional<Void>> deleteInfoLog() {
        return DataResult.success(service.deleteInfoLog());
    }
}
