package com.zf1976.ant.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;


/**
 * @author mac
 * @date 2021/2/11
 **/
@Configuration
@EnableWebFluxSecurity
public class ResourceServerSecurityConfiguration {

    private final ReactiveAuthorizationManager<AuthorizationContext> reactiveAuthorizationManager;

    public ResourceServerSecurityConfiguration(GatewayReactiveAuthorizationManager reactiveAuthenticationManager) {
        this.reactiveAuthorizationManager = reactiveAuthenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf()
                    // 关闭csrf
                    .disable()
                    // 允许跨域
                    .cors()
                    .and()
                    .authorizeExchange(authorizeExchangeSpec -> {
                        authorizeExchangeSpec.pathMatchers("/actuator/**","/oauth/**").permitAll()
                                             .anyExchange()
                                             // 自定义授权处理
                                             .access(this.reactiveAuthorizationManager);
                    })
                    .oauth2ResourceServer(oAuth2ResourceServerSpec -> {
                        oAuth2ResourceServerSpec.jwt(jwtSpec -> {
                            jwtSpec.jwtAuthenticationConverter(this.jwtConverter())
                                   .jwkSetUri("http://localhost:9000/oauth/token_key");
                        }).bearerTokenConverter(new ServerBearerTokenAuthenticationConverter());
                    });
        return httpSecurity.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
