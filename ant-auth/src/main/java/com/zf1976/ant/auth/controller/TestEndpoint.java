package com.zf1976.ant.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.auth.service.impl.ResourceService;
import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mac
 * @date 2021/4/10
 */
@RestController
@RequestMapping(value = "/oauth")
public class TestEndpoint {

    @Autowired
    private ResourceService service;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/resource")
    @PreAuthorize("hasRole('admin')")
    public Object resource(){
        return this.service.selectResourceNodeByPage(new Page<>(1,2));
    }
}
