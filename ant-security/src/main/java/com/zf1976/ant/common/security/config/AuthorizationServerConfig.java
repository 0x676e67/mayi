package com.zf1976.ant.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * @author mac
 * @date 2021/2/10
 **/
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthorizationServerConfig(PasswordEncoder passwordEncoder, DataSource dataSource) {
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
    }

    /**
     * 认证服务安全配置
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
        security.allowFormAuthenticationForClients();
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

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
    }
}
