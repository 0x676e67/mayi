package com.zf1976.ant.auth.safe.handler.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.ant.common.core.foundation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mac
 * Create by Ant on 2020/10/4 00:18
 */
public class SecurityLogoutSuccessHandler implements LogoutSuccessHandler {

    public final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        objectMapper.writeValue(httpServletResponse.getOutputStream(), Result.success());
        log.info("{}", "已登出");
    }
}
