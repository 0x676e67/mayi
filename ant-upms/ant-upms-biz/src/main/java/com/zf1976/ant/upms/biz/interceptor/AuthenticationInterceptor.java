package com.zf1976.ant.upms.biz.interceptor;

import com.zf1976.ant.common.security.support.session.SessionContextHolder;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.core.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ant
 * Create by Ant on 2021/3/23 4:03 PM
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     * an appropriate handler object, but before HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending an HTTP error or writing a custom response.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation returns {@code true}.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return  true;
        }
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            this.unauthenticatedHandler(response);
            return false;
        }
        try {
            return SessionContextHolder.readSession() != null;
        }catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e.getCause());
            }
            this.unauthenticatedHandler(response);
            return false;
        }
    }

    private void unauthenticatedHandler(HttpServletResponse response) {
        var fail = DataResult.fail(HttpStatus.UNAUTHORIZED);
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ATOM_XML_VALUE);
            JSONUtil.getJsonMapper().writeValue(response.getOutputStream(), fail);
        } catch (IOException ignored) {

        }
    }

}
