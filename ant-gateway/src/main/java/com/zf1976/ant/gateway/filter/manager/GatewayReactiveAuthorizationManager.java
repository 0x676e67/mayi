package com.zf1976.ant.gateway.filter.manager;

import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.constants.AuthConstants;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 授权管理器
 * @author mac
 * @date 2021/2/11
 **/
@SuppressWarnings({"rawtypes","unchecked"})
public class GatewayReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final RedisTemplate<Object, Map<Object, Object>> redisTemplate;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public GatewayReactiveAuthorizationManager(RedisTemplate<Object, Map<Object, Object>> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        final Object o = authorizationContext.getExchange()
                                             .getAttributes()
                                             .get(AuthConstants.OWNER);
        // 资源所有者放行所有
        if (o instanceof Boolean) {
            return Mono.just(new AuthorizationDecision((Boolean) o));
        }
        // options请求放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 白名单放行
        String requestUri = this.getRequestUri(request);
        for (String ignored : this.ignoreUri()) {
            if (pathMatcher.match(ignored, requestUri)) {
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        // 确保未配置情况下 认证中心放行
        if (pathMatcher.match(GatewayRouteConstants.AUTH_ROUTE, requestUri)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 非后台路径放行
        if (!ObjectUtils.nullSafeEquals(GatewayRouteConstants.ADMIN_ROUTE, this.getRequestUri(request))) {
            return Mono.just(new AuthorizationDecision(true));
        }
        final Authentication authentication = SecurityContextHolder.getContext()
                                                                   .getAuthentication();
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

    private Map<String, String> loadResourcePermission() {
        Map map = this.redisTemplate.opsForValue().get(Namespace.DYNAMIC);
        if (map != null) {
            return (Map) map.get(KeyConstants.RESOURCES);
        }
        return Collections.emptyMap();
    }

    private List<String> ignoreUri(){
        Map<Object, Object> map = this.redisTemplate.opsForValue().get(Namespace.DYNAMIC);
        if (map != null) {
            return (List) map.get(KeyConstants.ALLOW);
        }
        return Collections.emptyList();
    }

    private String formatPath(ServerHttpRequest serverHttpRequest) {
        String rawPath = this.getRequestUri(serverHttpRequest);
        return rawPath.replaceFirst(GatewayRouteConstants.TEST_PREFIX, StringUtil.ENMPTY);
    }

    private String getRequestUri(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getURI()
                                .getPath();
    }

}
