package com.zf1976.ant.auth.filter;

import com.nimbusds.jose.JWSObject;
import com.power.common.util.StringUtil;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.AntPathMatcher;
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
 * Create by Ant on 2021/2/18 11:55 PM
 */
public class Oauth2TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    private static final String TOKEN_PREFIX = "Bearer ";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final SecurityProperties config = ApplicationConfigUtils.getSecurityProperties();

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 无请求头直接放行 或在放行名单 直接放行
        final String token = this.getToken(request);
        if (StringUtil.isEmpty(token) || validateAllowUri(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final TokenStore tokenStore = SecurityContextHolder.getShareObject(TokenStore.class);

        final JWSObject parse = JWSObject.parse(token);
    }


    /**
     * 验证请求uri是否在放行名单
     *
     * @param request request
     * @return boolean
     */
    private boolean validateAllowUri(HttpServletRequest request) {
        // 匿名放行uri
        String[] allowUri = config.getAllowUri();
        // 请求uri
        String requestUri = request.getRequestURI();
        return Arrays.stream(allowUri).anyMatch(url -> MATCHER.match(url, requestUri));
    }

    /**
     * 获取请求头 token
     *
     * @param request 请求
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(token)) {
            return token.replace(TOKEN_PREFIX, StringUtil.ENMPTY);
        }
        return null;
    }
}
