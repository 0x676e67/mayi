package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.query.SessionQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.SessionVO;
import com.zf1976.ant.upms.biz.service.SysOnlineService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2021/1/20
 **/
@RestController
@RequestMapping("/api/online")
public class OnlineController {

    private final SysOnlineService service;

    public OnlineController(SysOnlineService sysOnlineService) {
        this.service = sysOnlineService;
    }

    @PostMapping("/page")
    public ResultData<IPage<SessionVO>> selectSessionList(@RequestBody RequestPage<SessionQueryParam> requestPage) {
        return ResultData.success(this.service.selectSessionPage(requestPage));
    }

    @DeleteMapping("/delete")
    public ResultData<Optional<Void>> deleteSession(@RequestBody Set<Long> ids) {
        return ResultData.success(this.service.forceOffline(ids));
    }
}
