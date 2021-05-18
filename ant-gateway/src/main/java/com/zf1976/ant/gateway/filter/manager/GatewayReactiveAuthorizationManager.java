package com.zf1976.ant.gateway.filter.manager;

import com.nimbusds.jose.JWSObject;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.constants.KeyConstants;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.gateway.GatewayRouteConstants;
import net.minidev.json.JSONArray;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
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
        // 校验是否为owner
        final Object owner = authorizationContext.getExchange()
                                                 .getAttributes()
                                                 .get(AuthConstants.OWNER);
        // options请求放行
        if (Objects.requireNonNull(request.getMethod())
                   .matches(HttpMethod.OPTIONS.name())) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 资源所有者放行所有
        if (owner instanceof Boolean && (Boolean) owner) {
            return Mono.just(new AuthorizationDecision(true));
        }
        String requestUri = this.getRequestUri(request);
        // 确保未配置情况下 认证中心放行
        if (this.pathMatcher.match(GatewayRouteConstants.AUTH_ROUTE, requestUri)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 非后台路径放行
        if (!ObjectUtils.nullSafeEquals(GatewayRouteConstants.ADMIN_ROUTE, requestUri)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 白名单放行
        for (String ignored : this.ignoreUri()) {
            if (this.pathMatcher.match(ignored, requestUri)) {
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        // 提取系统资源权限
        Set<String> permissions = this.extractPermission(request)
                                      .stream()
                                      .map(GrantedAuthority::getAuthority)
                                      .collect(Collectors.toSet());
        return mono.filter(Authentication::isAuthenticated)
                   .flatMapIterable(Authentication::getAuthorities)
                   .map(GrantedAuthority::getAuthority)
                   .all(permissions::contains)
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
        String requestUri = this.getRequestUri(serverHttpRequest);
        for (Map.Entry<String, String> entry : resourcePermission.entrySet()) {
            boolean condition = false;
            // eq匹配
            if (ObjectUtils.nullSafeEquals(entry.getKey(), requestUri)) {
                condition = true;
                // 进行模式匹配
            } else if (this.pathMatcher.match(entry.getKey(), requestUri)) {
                condition = true;
            }
            if (condition) {
                return AuthorityUtils.commaSeparatedStringToAuthorityList(entry.getValue());
            }
        }
        // 返回空
        return AuthorityUtils.commaSeparatedStringToAuthorityList("");
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


    private String getRequestUri(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getURI().getPath();
    }

}
