package com.zf1976.mayi.auth.endpoint;

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


    @GetMapping("/test")
    public String test() {
        return "";
    }

}
