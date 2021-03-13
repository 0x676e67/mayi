package com.zf1976.ant.auth.endpoint;

import com.zf1976.ant.common.core.property.SecurityProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author mac
 * @date 2021/3/13
 **/
@RestController
@RequestMapping("/security")
public class SecurityEndpoint {

    private final SecurityProperties securityProperties;

    public SecurityEndpoint(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @GetMapping("/allow-uri")
    List<String> getAllowUri() {
        return Arrays.asList(this.securityProperties.getIgnoreUri());
    }

}
