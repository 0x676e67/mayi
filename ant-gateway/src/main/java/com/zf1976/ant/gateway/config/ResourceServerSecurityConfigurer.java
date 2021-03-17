package com.zf1976.ant.gateway.config;

import com.zf1976.ant.common.core.constants.KeyConstants;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.gateway.filter.Oauth2TokenAuthenticationFilter;
import com.zf1976.ant.gateway.filter.manager.GatewayReactiveAuthorizationManager;
import com.zf1976.ant.gateway.properties.AuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.RedisTemplate;
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
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @date 2021/2/11
 **/
@Configuration
@EnableWebFluxSecurity
@SuppressWarnings("all")
public class ResourceServerSecurityConfigurer {

    private final AuthProperties properties;
    private final RedisTemplate<Object, Map<Object, Object>> redisTemplate;
    private final HttpClient gatewayHttpClient;

    public ResourceServerSecurityConfigurer(AuthProperties properties,
                                            RedisTemplate mapRedisTemplate,
                                            HttpClient gatewayHttpClient) {
        this.properties = properties;
        this.redisTemplate = mapRedisTemplate;
        this.gatewayHttpClient = gatewayHttpClient;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        Oauth2TokenAuthenticationFilter tokenAuthenticationFilter = new Oauth2TokenAuthenticationFilter(properties.getJwtCheckUri());
        tokenAuthenticationFilter.setHttpClient(this.gatewayHttpClient);
        return httpSecurity.httpBasic()
                           .disable()
                           .csrf()
                           // 关闭csrf
                           .disable()
                           // 允许跨域
                           .cors()
                           .and()
                           // 关闭表单登录
                           .formLogin()
                           .disable()
                           .authorizeExchange(authorizeExchangeSpec -> {
                               authorizeExchangeSpec.pathMatchers("/actuator/**", "/oauth/**")
                                                    .permitAll()
                                                    .anyExchange()
                                                    // 自定义访问授权处理
                                                    .access(new GatewayReactiveAuthorizationManager(redisTemplate, this.IgnoreUri()))
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
                           .addFilterBefore(tokenAuthenticationFilter,
                                   SecurityWebFiltersOrder.HTTP_BASIC)
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

    private List<String> IgnoreUri(){
        Map<Object, Object> map = this.redisTemplate.opsForValue().get(Namespace.DYNAMIC);
        return List.class.cast(map.get(KeyConstants.ALLOW));
    }

}
