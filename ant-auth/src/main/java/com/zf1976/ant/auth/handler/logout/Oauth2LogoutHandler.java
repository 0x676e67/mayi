package com.zf1976.ant.auth.handler.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.ant.auth.cache.session.Session;
import com.zf1976.ant.auth.filter.manager.SessionContextHolder;
import com.zf1976.ant.auth.exception.ExpiredJwtException;
import com.zf1976.ant.auth.exception.IllegalAccessException;
import com.zf1976.ant.auth.exception.IllegalJwtException;
import com.zf1976.ant.auth.enums.AuthenticationState;
import com.zf1976.ant.auth.JwtTokenProvider;
import com.zf1976.ant.common.core.foundation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author mac
 * Create by Ant on 2020/10/4 01:03
 */
@SuppressWarnings("rawtypes")
public class Oauth2LogoutHandler implements LogoutHandler {

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 校验请求方法
     *
     * @param request request
     */
    private void validateMethod(HttpServletRequest request) {
        final String method = request.getMethod();
        if (StringUtils.isEmpty(method)) {
            throw new IllegalAccessException(AuthenticationState.ILLEGAL_ACCESS);
        }
        if (!method.equals(HttpMethod.DELETE.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        AuthenticationException authenticationException = null;
        try {
            validateMethod(httpServletRequest);
            String token = JwtTokenProvider.getRequestToken(httpServletRequest);
            // 一次验证token是否过期 可能未过期但已经销毁 即使本地存在 已过期也不允许使用
            if (StringUtils.isEmpty(token) || JwtTokenProvider.validateExpired(token)) {
                throw new IllegalAccessException(AuthenticationState.ILLEGAL_ACCESS);
            }
            // 二次验证token是否过期
            Long id = JwtTokenProvider.getSessionId(token);
            final Session session = SessionContextHolder.get(id);
            if (Objects.isNull(session) || !StringUtils.hasText(token)) {
                throw new ExpiredJwtException(AuthenticationState.EXPIRED_JWT);
            }
            SessionContextHolder.removeSession(token);
        } catch (ExpiredJwtException | IllegalJwtException | IllegalAccessException | AuthenticationServiceException e) {
            authenticationException = e;
        }
        if (authenticationException != null) {
            SecurityContextHolder.clearContext();
            try {
                this.unSuccessLogoutHandler(httpServletRequest, httpServletResponse, authenticationException);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 登出失败处理
     *
     * @param httpServletRequest request
     * @param httpServletResponse  response
     * @param e 异常
     * @throws IOException 向上抛异常
     */
    private void unSuccessLogoutHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        Result fail = null;
        if (e instanceof ExpiredJwtException) {
            ExpiredJwtException exception = (ExpiredJwtException) e;
            fail = Result.fail(exception.getValue(), exception.getReasonPhrase());
        }else if (e instanceof IllegalAccessException) {
            IllegalAccessException exception = (IllegalAccessException) e;
            fail = Result.fail(exception.getValue(), exception.getReasonPhrase());
        } else if (e instanceof IllegalJwtException) {
            IllegalJwtException exception = (IllegalJwtException) e;
            fail = Result.fail(exception.getValue(), exception.getReasonPhrase());
        } else if (e instanceof AuthenticationServiceException) {
            AuthenticationServiceException exception = (AuthenticationServiceException) e;
            fail = Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        } else {
            fail = Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        fail.setPath(httpServletRequest.getRequestURI());
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(httpServletResponse.getOutputStream(), fail);
    }

}
