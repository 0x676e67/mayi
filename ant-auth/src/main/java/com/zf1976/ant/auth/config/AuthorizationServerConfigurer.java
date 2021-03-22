package com.zf1976.ant.auth.config;

import com.zf1976.ant.auth.enhance.JdbcClientDetailsServiceEnhancer;
import com.zf1976.ant.auth.enhance.JwtTokenEnhancer;
import com.zf1976.ant.auth.enhance.RedisTokenStoreEnhancer;
import com.zf1976.ant.auth.filter.provider.DaoAuthenticationEnhancerProvider;
import com.zf1976.ant.auth.handler.access.Oauth2AccessDeniedHandler;
import com.zf1976.ant.auth.handler.access.Oauth2AuthenticationEntryPoint;
import com.zf1976.ant.auth.interceptor.EndpointReturnInterceptor;
import com.zf1976.ant.common.security.SecurityContextHolder;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.Assert;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;

/**
 * @author mac
 * @date 2021/2/10
 **/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter implements SmartLifecycle {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer;
    private final RedisTemplate<Object,Object> template;
    private final KeyPair keyPair;
    private AuthorizationServerSecurityConfigurer authorizationServerSecurityConfigurer;
    private boolean isRunning = false;

    public AuthorizationServerConfigurer(UserDetailsService userDetailsService,
                                         PasswordEncoder passwordEncoder,
                                         AuthenticationManager authenticationManager,
                                         RedisTemplate<Object, Object> template,
                                         KeyPair keyPair,
                                         JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer) {
        this.userDetailsService =  userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.template = template;
        this.keyPair = keyPair;
        this.jdbcClientDetailsServiceEnhancer = jdbcClientDetailsServiceEnhancer;
        SecurityContextHolder.setShareObject(JdbcClientDetailsServiceEnhancer.class, this.jdbcClientDetailsServiceEnhancer);
    }

    /**
     * 允许表单认证
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        this.authorizationServerSecurityConfigurer = security;
        Assert.notNull(this.authorizationServerSecurityConfigurer,"authorizationServerSecurityConfigurer object cannot been null");
        security.allowFormAuthenticationForClients()
                .authenticationEntryPoint(new Oauth2AuthenticationEntryPoint())
                .accessDeniedHandler(new Oauth2AccessDeniedHandler())
                // oauth/check_token 允许访问
                .checkTokenAccess("permitAll()")
                // oauth/token_key 公开密钥
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(passwordEncoder)
                .addTokenEndpointAuthenticationFilter((request,response, chain) -> {
                    chain.doFilter(request, response);
                });
    }

    /**
     * 客户端服务配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        this.jdbcClientDetailsServiceEnhancer.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(this.jdbcClientDetailsServiceEnhancer);
    }

    /**
     * 认证端点
     * refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
     * 1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
     * 2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        RedisConnectionFactory redisConnectionFactory = this.template.getRequiredConnectionFactory();
        TokenStore tokenStore = new RedisTokenStoreEnhancer(redisConnectionFactory).enhance();
        SecurityContextHolder.setShareObject(TokenStore.class, tokenStore);
        endpoints.authenticationManager(authenticationManager)
                 .tokenStore(tokenStore)
                 .allowedTokenEndpointRequestMethods(HttpMethod.POST)
                 .tokenEnhancer(tokenEnhancerChain())
                 .accessTokenConverter(jwtAccessTokenConverter())
                 .userDetailsService(userDetailsService)
                 .reuseRefreshTokens(false)
                 .addInterceptor(new EndpointReturnInterceptor());

    }

    private JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(this.keyPair);
        return jwtAccessTokenConverter;
    }

    private TokenEnhancerChain tokenEnhancerChain() {
        var enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = Arrays.asList(new JwtTokenEnhancer(), jwtAccessTokenConverter());
        enhancerChain.setTokenEnhancers(enhancerList);
        return enhancerChain;
    }


    /**
     * 所有bean初始化完成
     * 增强Oauth Provide客户端处理
     */
    @Override
    public void start() {
        Assert.notNull(this.authorizationServerSecurityConfigurer, "authorizationServerSecurityConfigurer cannot been null!");
        ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(this.jdbcClientDetailsServiceEnhancer);
        DaoAuthenticationEnhancerProvider daoClientAuthenticationEnhancerProvider = DaoAuthenticationEnhancerProvider.builder()
                                                                                                                     .setPasswordEncoder(this.passwordEncoder)
                                                                                                                     .setUserDetailsService(clientDetailsUserDetailsService)
                                                                                                                     .build();
        this.authorizationServerSecurityConfigurer.and().authenticationProvider(daoClientAuthenticationEnhancerProvider);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }


    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

}
