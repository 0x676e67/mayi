package com.zf1976.ant.auth.config;

import com.zf1976.ant.auth.AuthorizationConstants;
import com.zf1976.ant.auth.safe.handler.access.SecurityAccessDeniedHandler;
import com.zf1976.ant.auth.safe.handler.access.SecurityAuthenticationEntryPoint;
import com.zf1976.ant.auth.service.SecurityUserDetailsServiceImpl;

import com.zf1976.ant.common.core.dev.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mac
 * @date 2021/2/10
 **/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    private final SecurityUserDetailsServiceImpl securityUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<Object,Object> template;
    private final SecurityProperties securityProperties;

    public AuthorizationServerConfigurer(SecurityUserDetailsServiceImpl securityUserDetailsService,
                                         PasswordEncoder passwordEncoder,
                                         DataSource dataSource,
                                         AuthenticationManager authenticationManager,
                                         RedisTemplate<Object, Object> template,
                                         SecurityProperties securityProperties) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
        this.authenticationManager = authenticationManager;
        this.template = template;
        this.securityProperties = securityProperties;
    }

    /**
     * 允许表单认证
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .authenticationEntryPoint(new SecurityAuthenticationEntryPoint())
                .accessDeniedHandler(new SecurityAccessDeniedHandler())
                // oauth/check_token公开
                .checkTokenAccess("permitAll()")
                // oauth/token_key 公开密钥
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(passwordEncoder);
               // .addTokenEndpointAuthenticationFilter(new SignatureAuthenticationFilter());
    }

    /**
     * 客户端服务配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        clients.withClientDetails(jdbcClientDetailsService);
    }

    /**
     * 认证端点
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        RedisTokenStore redisTokenStore = new RedisTokenStore(this.getRedisConnectionFactory());
        redisTokenStore.setPrefix(AuthorizationConstants.PROJECT_OAUTH_TOKEN);
        endpoints.authenticationManager(authenticationManager)
                 .tokenServices(tokenServiceEnhancer())
                 .accessTokenConverter(jwtAccessTokenConverter())
                 .tokenEnhancer(tokenEnhancer())
                 .tokenStore(redisTokenStore)
                 // refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                 // 1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
                 // 2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而
                 .reuseRefreshTokens(false)
                 .userDetailsService(securityUserDetailsService);
    }

    public AuthorizationServerTokenServices tokenServiceEnhancer() {
        final DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        // 令牌默认有效期2小时
        tokenServices.setAccessTokenValiditySeconds(securityProperties.getTokenExpiredTime().intValue());
        // 刷新令牌默认有效期3天
        tokenServices.setRefreshTokenValiditySeconds(securityProperties.getTokenRestore().intValue());
        return tokenServices;
    }

    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> map = new HashMap<>(2);
            final Authentication userAuthentication = authentication.getUserAuthentication();
            final Set<String> authorityListToSet = AuthorityUtils.authorityListToSet(userAuthentication.getAuthorities());
            map.put(securityProperties.getTokenAuthoritiesKey(), authorityListToSet);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }

    /**
     * 从classpath下的密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        final char[] secretChar = securityProperties.getRsaSecret().toCharArray();
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), secretChar );
        return factory.getKeyPair("jwt", secretChar);
    }

    public RedisConnectionFactory getRedisConnectionFactory() {
        return this.template.getRequiredConnectionFactory();
    }

}
