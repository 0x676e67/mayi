package com.zf1976.mayi.upms.biz.controller;

import com.zf1976.mayi.common.log.annotation.Log;
import com.zf1976.mayi.common.log.dao.SysLogDao;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author mac
 * @date 2020/12/24
 **/
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private SysLogDao sysLogDao;

    @Log(description = "测试A接口")
    @RequestMapping(method = RequestMethod.GET, path = "/demo")
    public void testA(@RequestParam String description) {
        throw new RuntimeException(this.getClass().getName());
    }

    @Log(description = "测试B接口")
    @GetMapping("/{demo}")
    public String testB(@PathVariable String demo){
        return demo;
    }
}
