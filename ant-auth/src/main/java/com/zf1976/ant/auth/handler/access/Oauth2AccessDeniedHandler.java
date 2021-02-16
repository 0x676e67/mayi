package com.zf1976.ant.auth.handler.access;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mac
 * Create by Ant on 2020/9/3 下午8:48
 */

public class Oauth2AccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 拒绝访问处理 当用户访问受保护的资源时 发送403处理
     *
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @param e exception
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) {
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
