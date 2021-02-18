package com.zf1976.ant.auth.config;

import com.zf1976.ant.auth.enhance.JwtTokenEnhancer;
import com.zf1976.ant.auth.enhance.RedisTokenStoreEnhancer;
import com.zf1976.ant.auth.handler.access.Oauth2AccessDeniedHandler;
import com.zf1976.ant.auth.handler.access.Oauth2AuthenticationEntryPoint;
import com.zf1976.ant.auth.interceptor.EndpointReturnInterceptor;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;

/**
 * @author mac
 * @date 2021/2/10
 **/
@Configuration
@EnableAuthorizationServer
@Order(-111111111)
@ConditionalOnBean(SecurityProperties.class)
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<Object,Object> template;
    private final SecurityProperties securityProperties;
    private final KeyPair keyPair;

    public AuthorizationServerConfigurer(UserDetailsService userDetailsService,
                                         PasswordEncoder passwordEncoder,
                                         DataSource dataSource,
                                         AuthenticationManager authenticationManager,
                                         RedisTemplate<Object, Object> template,
                                         SecurityProperties securityProperties,
                                         KeyPair keyPair) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
        this.authenticationManager = authenticationManager;
        this.template = template;
        this.securityProperties = securityProperties;
        this.keyPair = keyPair;
    }

    /**
     * 允许表单认证
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .authenticationEntryPoint(new Oauth2AuthenticationEntryPoint())
                .accessDeniedHandler(new Oauth2AccessDeniedHandler())
                // oauth/check_token公开
                .checkTokenAccess("permitAll()")
                // oauth/token_key 公开密钥
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(passwordEncoder);
    }

    /**
     * 客户端服务配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(jdbcClientDetailsService);
    }

    /**
     * 认证端点
     * refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
     * 1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
     * 2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        RedisTokenStoreEnhancer tokenStoreEnhancer = new RedisTokenStoreEnhancer(this.getRedisConnectionFactory());
        TokenStore tokenStore = tokenStoreEnhancer.enhance();
        endpoints.authenticationManager(authenticationManager)
                 .tokenStore(tokenStore)
                 .allowedTokenEndpointRequestMethods(HttpMethod.POST)
                 .tokenEnhancer(tokenEnhancerChain())
                 .accessTokenConverter(jwtAccessTokenConverter())
                 .userDetailsService(userDetailsService)
                 .reuseRefreshTokens(false)
                 .addInterceptor(new EndpointReturnInterceptor());

    }

    public TokenEnhancerChain tokenEnhancerChain() {
        final TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        final List<TokenEnhancer> asList = Arrays.asList(new JwtTokenEnhancer(), jwtAccessTokenConverter());
        enhancerChain.setTokenEnhancers(asList);
        return enhancerChain;
    }

    private JwtAccessTokenConverter jwtAccessTokenConverter(){
        final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(this.keyPair);
        jwtAccessTokenConverter.setSigningKey(securityProperties.getTokenBase64Secret());
        return jwtAccessTokenConverter;
    }

    private RedisConnectionFactory getRedisConnectionFactory() {
        return this.template.getRequiredConnectionFactory();
    }

}
