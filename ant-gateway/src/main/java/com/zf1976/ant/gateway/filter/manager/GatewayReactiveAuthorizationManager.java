package com.zf1976.ant.gateway.filter.manager;

import com.google.common.collect.Sets;
import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.constants.KeyConstants;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.gateway.GatewayRouteConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/2/11
 **/
@SuppressWarnings("rawtypes")
public class GatewayReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RedisTemplate<Object, Map<Object, Object>> redisTemplate;
    private Collection<String> ignoreUri;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public GatewayReactiveAuthorizationManager(RedisTemplate<Object, Map<Object, Object>> redisTemplate,
                                               Collection<String> ignoreUri) {
        this.redisTemplate = redisTemplate;
        this.ignoreUri = ignoreUri;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        // options请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 白名单放行
        String requestUri = this.getRequestUri(request);
        for (String ignored : ignoreUri) {
            if (pathMatcher.match(ignored, requestUri)) {
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        // 认证中心路由放行
        if (pathMatcher.match(GatewayRouteConstants.AUTH_ROUTE, requestUri)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 非后台路径放行
        if (!ObjectUtils.nullSafeEquals(GatewayRouteConstants.ADMIN_ROUTE, this.getRequestUri(request))) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 提取系统资源权限
        Set<String> permissionSet = this.extractPermission(request)
                                  .stream()
                                  .map(GrantedAuthority::getAuthority)
                                  .collect(Collectors.toSet());
        return mono.filter(Authentication::isAuthenticated)
                   .flatMapIterable(Authentication::getAuthorities)
                   .map(GrantedAuthority::getAuthority)
                   .any(permissionSet::contains)
                   .map(AuthorizationDecision::new)
                   .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * 提取系统权限
     *
     * @param serverHttpRequest request
     * @return permission list
     */
    private List<GrantedAuthority> extractPermission(ServerHttpRequest serverHttpRequest) {
        Map<String, String> resourcePermission = this.loadResourcePermission();
        String path = this.formatPath(serverHttpRequest);
        String permission = resourcePermission.get(path);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(permission);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private Map<String, String> loadResourcePermission() {
        Map map = this.redisTemplate.opsForValue().get(Namespace.DYNAMIC);
        if (map != null) {
            return (Map) map.get(KeyConstants.RESOURCES);
        }
        return Collections.emptyMap();
    }

    private String formatPath(ServerHttpRequest serverHttpRequest) {
        String rawPath = this.getRequestUri(serverHttpRequest);
        return rawPath.replaceFirst(GatewayRouteConstants.TEST_PREFIX, StringUtil.ENMPTY);
    }

    private String getRequestUri(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getURI()
                                .getPath();
    }

    public GatewayReactiveAuthorizationManager setIgnoreUri(Collection<String> ignoreUri) {
        this.ignoreUri = ignoreUri;
        return this;
    }

    public void addIgnoreUri(String ...ignoreUri) {
        this.ignoreUri.addAll(Sets.newHashSet(ignoreUri));
    }
}
