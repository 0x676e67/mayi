package com.zf1976.ant.upms.biz.config;

import com.zf1976.ant.common.security.support.session.DistributedSessionManager;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * @author mac
 * @date 2021/4/8
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            final String token = DistributedSessionManager.getSession()
                                                          .getToken();
            requestTemplate.header(HttpHeaders.AUTHORIZATION, token);
        };
    }

}
