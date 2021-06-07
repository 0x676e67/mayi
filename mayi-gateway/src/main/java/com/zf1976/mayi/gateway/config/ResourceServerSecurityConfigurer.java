package com.zf1976.mayi.gateway.config;

import com.zf1976.mayi.gateway.filter.OAuth2TokenAuthenticationFilter;
import com.zf1976.mayi.gateway.filter.manager.GatewayReactiveAuthorizationManager;
import com.zf1976.mayi.gateway.properties.AuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author mac
 * @date 2021/2/11
 **/
@Configuration
@EnableWebFluxSecurity
public class ResourceServerSecurityConfigurer {

    private final AuthProperties properties;
    private final RedisTemplate<Object, Map<Object, Object>> redisTemplate;

    public ResourceServerSecurityConfigurer(AuthProperties properties,
                                            RedisTemplate<Object, Map<Object, Object>> mapRedisTemplate) {
        this.properties = properties;
        this.redisTemplate = mapRedisTemplate;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity.httpBasic()
                           .disable()
                           // 关闭csrf
                           .csrf().disable()
                           // 允许跨域
                           .cors()
                           .and()
                           // 关闭表单登录
                           .formLogin()
                           .disable()
                           .authorizeExchange(authorizeExchangeSpec -> {
                               authorizeExchangeSpec.pathMatchers("/actuator/**", "/oauth/**","/avatar/**").permitAll()
                                                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                    .anyExchange()
                                                    // 自定义访问授权处理
                                                    .access(new GatewayReactiveAuthorizationManager(redisTemplate))
                                                    .and()
                                                    .exceptionHandling()
                                                    .accessDeniedHandler(new BearerTokenServerAccessDeniedHandler())
                                                    .authenticationEntryPoint(new BearerTokenServerAuthenticationEntryPoint());
                           })
                           .oauth2ResourceServer(oAuth2ResourceServerSpec -> {
                               oAuth2ResourceServerSpec.jwt(jwtSpec -> {
                                   jwtSpec.jwtAuthenticationConverter(this.jwtConverter())
                                          .jwkSetUri(properties.getJwkSetUri());
                               }).bearerTokenConverter(new ServerBearerTokenAuthenticationConverter());

                           })
                           .addFilterBefore(new OAuth2TokenAuthenticationFilter(properties.getJwtCheckUri()), SecurityWebFiltersOrder.HTTP_BASIC)
                           .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
