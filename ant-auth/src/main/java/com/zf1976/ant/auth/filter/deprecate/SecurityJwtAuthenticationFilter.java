package com.zf1976.ant.auth.filter.deprecate;

import com.zf1976.ant.auth.JwtTokenProvider;
import com.zf1976.ant.auth.SessionContextHolder;
import com.zf1976.ant.auth.cache.session.Session;
import com.zf1976.ant.auth.enums.AuthenticationState;
import com.zf1976.ant.auth.exception.ExpiredJwtException;
import com.zf1976.ant.auth.exception.IllegalAccessException;
import com.zf1976.ant.auth.exception.IllegalJwtException;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author ant
 * Create by Ant on 2020/9/14 3:43 下午
 */
public class SecurityJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityJwtAuthenticationFilter.class);
    private static final SecurityProperties CONFIG = ApplicationConfigUtils.getSecurityProperties();
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * 验证请求uri是否在放行名单
     *
     * @param request request
     * @return boolean
     */
    private static boolean validateAllowUri(HttpServletRequest request) {
        // 匿名放行uri
        String[] allowUri = ApplicationConfigUtils.getSecurityProperties().getAllowUri();
        // 请求uri
        String requestUri = request.getRequestURI();
        return Arrays.stream(allowUri)
                     .anyMatch(url -> MATCHER.match(url, requestUri));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull FilterChain filterChain) throws IOException, ServletException {
        // 无请求头直接放行 或在放行名单 直接放行
        if (!hasMethod(httpServletRequest) || validateAllowUri(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AuthenticationException authenticationException = null;
        try {
            String token = this.validateToken(httpServletRequest, httpServletResponse);
            Authentication authentication = JwtTokenProvider.resolveAuthentication(token);
            SecurityContextHolder.getContext()
                                 .setAuthentication(authentication);
        } catch (ExpiredJwtException | IllegalJwtException | IllegalAccessException e) {
            authenticationException = e;
        } finally {
            stopWatch.stop();
            if (LOG.isDebugEnabled()) {
                LOG.debug("jwt filter time:{}", stopWatch.getTotalTimeSeconds());
            }
        }
        if (authenticationException != null) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean hasMethod(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return !StringUtils.isEmpty(header);
    }

    /**
     * 校验并返回token
     *
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @return token
     * @throws ExpiredJwtException    向上抛异常
     * @throws IllegalAccessException 向上抛异常
     */
    private String validateToken(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse) throws ExpiredJwtException, IllegalAccessException {
        String token = JwtTokenProvider.getRequestToken(httpServletRequest);
        if (StringUtils.isEmpty(token) || !StringUtils.hasText(token) || JwtTokenProvider.validateExpired(token)) {
            throw new IllegalAccessException(AuthenticationState.ILLEGAL_ACCESS);
        }
        Long id = JwtTokenProvider.getSessionId(token);
        Session session = SessionContextHolder.readSession(token);
        long remainingTime;
        if (session != null) {
            if (CONFIG.getEnableRestore() & (remainingTime = SessionContextHolder.getExpired(id)) > 0) {
                String refreshToken;
                if (remainingTime < CONFIG.getTokenDetect()) {
                    refreshToken = JwtTokenProvider.refreshToken(token, CONFIG.getTokenRestore());
                    remainingTime = remainingTime + CONFIG.getTokenRestore();
                } else {
                    refreshToken = token;
                }
                Assert.notNull(refreshToken, "token cannot been null");
                session.setToken(refreshToken);
                // 更新session
                SessionContextHolder.refreshSession(id, session, remainingTime);
                httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, refreshToken);
                return refreshToken;
            } else if (remainingTime > 0) {
                return token;
            }
        }
        throw new ExpiredJwtException(AuthenticationState.EXPIRED_JWT);
    }
}
