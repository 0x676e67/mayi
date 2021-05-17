package com.zf1976.ant.upms.biz.config;

import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author mac
 * @date 2021/4/8
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            final Collection<String> tokenHeader = requestTemplate.headers().get(HttpHeaders.AUTHORIZATION);
            if (CollectionUtils.isEmpty(tokenHeader)) {
                final Session currentSession = SessionManagement.getSession();
                requestTemplate.header(HttpHeaders.AUTHORIZATION, currentSession.getToken());
            }
        };
    }

}
