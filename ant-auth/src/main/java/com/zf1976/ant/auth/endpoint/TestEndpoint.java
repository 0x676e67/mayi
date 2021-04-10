package com.zf1976.ant.auth.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mac
 * @date 2021/4/10
 */
@RestController
public class TestEndpoint {

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
