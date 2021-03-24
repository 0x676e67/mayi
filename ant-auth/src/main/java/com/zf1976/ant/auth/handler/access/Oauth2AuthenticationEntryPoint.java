package com.zf1976.ant.auth.handler.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.ant.common.core.foundation.DataResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mac
 * Create by Ant on 2020/9/3 下午8:52
 */
@SuppressWarnings("rawtypes")
public class Oauth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 当用户访问安全的资源时 未提供凭证 或者 无效凭证时 发送401处理
     *
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @param e exception
     * @throws IOException 向上抛异常
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        DataResult fail = DataResult.fail(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        fail.setPath(httpServletRequest.getRequestURI());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(httpServletResponse.getOutputStream(), fail);
    }
}
