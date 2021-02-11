package com.zf1976.ant.common.security.config;

import com.zf1976.ant.common.security.safe.filter.SignatureAuthenticationFilter;
import com.zf1976.ant.common.security.serialize.JacksonRedisTokenStoreSerializationStrategy;
import com.zf1976.ant.common.security.safe.service.SecurityUserDetailsServiceImpl;

import com.zf1976.ant.common.security.token.JwtTokenEnhancer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author mac
 * @date 2021/2/10
 **/
@Configuration
@EnableAuthorizationServer
public class Oauth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final SecurityUserDetailsServiceImpl securityUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<Object,Object> template;

    public Oauth2AuthorizationServerConfig(SecurityUserDetailsServiceImpl securityUserDetailsService,
                                           PasswordEncoder passwordEncoder,
                                           DataSource dataSource,
                                           AuthenticationManager authenticationManager,
                                           RedisTemplate<Object, Object> template) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
        this.authenticationManager = authenticationManager;
        this.template = template;
    }

    /**
     * 允许表单认证
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .passwordEncoder(passwordEncoder)
                .addTokenEndpointAuthenticationFilter(new SignatureAuthenticationFilter());
    }

    /**
     * 客户端服务配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        clients.withClientDetails(jdbcClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        RedisTokenStore redisTokenStore = new RedisTokenStore(this.getRedisConnectionFactory());
        endpoints.authenticationManager(authenticationManager)
                 .tokenEnhancer(new JwtTokenEnhancer())
                 .tokenStore(redisTokenStore)
                 .userDetailsService(securityUserDetailsService);
    }

    public RedisConnectionFactory getRedisConnectionFactory() {
        return this.template.getRequiredConnectionFactory();
    }
}
