package com.zf1976.ant.upms.biz.interceptor;

import com.zf1976.ant.common.component.session.SessionContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ant
 * Create by Ant on 2021/3/23 4:03 PM
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

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
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            return false;
        }
        return SessionContextHolder.readSession() != null;
    }

}
