package com.zf1976.ant.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.core.property.SecurityProperties;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.AntPathMatcher;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SecurityProperties config = SpringContextHolder.getBean(SecurityProperties.class);
    private final TokenStore tokenStore = SecurityContextHolder.getShareObject(TokenStore.class);
    private final TokenExtractor tokenExtractor = new BearerTokenExtractor();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        // 无请求头直接放行 或在放行名单 直接放行
        Authentication authentication = this.tokenExtractor.extract(request);
        String accessToken;
        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            accessToken = (String) authentication.getPrincipal();
            if (StringUtil.isEmpty(accessToken) || validateAllowUri(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.error("Jwt filter error at: {}", this.getFilterName());
            }
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        // 校验token
        try {
            OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);
            if (oAuth2AccessToken == null) {
                throw new InvalidTokenException("Token was not recognised");
            } else if (oAuth2AccessToken.isExpired()) {
                throw new InvalidTokenException("Token has expired");
            }
            OAuth2Authentication oAuth2Authentication = this.tokenStore.readAuthentication(oAuth2AccessToken);
            SecurityContext context = SecurityContextHolder.getContext();
            if (context != null) {
                if (context.getAuthentication() != null) {
                    filterChain.doFilter(request, response);
                    return;
                }
                context.setAuthentication(oAuth2Authentication);
            } else {
                throw new ServletException("Authentication error at:" + this.getFilterName());
            }
        } catch (InvalidTokenException e) {
            this.handleException(e, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 处理异常
     *
     * @param e exception
     * @param response response
     * @throws Exception throw
     */
    public void handleException(Exception e, HttpServletResponse response) throws Exception {
        if (this.log.isDebugEnabled()) {
            this.logger.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        }
        InvalidTokenException e400 = new InvalidTokenException(e.getMessage());
        response.setContentType("application/json");
        response.setStatus(e400.getHttpErrorCode());
        objectMapper.writeValue(response.getOutputStream(), e400);
    }

    /**
     * 验证请求uri是否在放行名单
     *
     * @param request request
     * @return boolean
     */
    private boolean validateAllowUri(HttpServletRequest request) {
        // 匿名放行uri
        String[] allowUri = config.getIgnoreUri();
        // 请求uri
        String requestUri = request.getRequestURI();
        return Arrays.stream(allowUri).anyMatch(url -> pathMatcher.match(url, requestUri));
    }
}
