package com.zf1976.ant.gateway.manager;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

/**
 * @author mac
 * @date 2021/2/11
 **/
@Component
public class GatewayReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        return mono.filter(Authentication::isAuthenticated)
                   .flatMapIterable(Authentication::getAuthorities)
                   .map(GrantedAuthority::getAuthority)
                   .any(p -> true)
                   .map(AuthorizationDecision::new)
                   .defaultIfEmpty(new AuthorizationDecision(false));
    }

//    @Override
//    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
//        return this.check(authentication, object);
//    }
}
