package com.zf1976.mayi.auth.config;

import com.zf1976.mayi.auth.SecurityContextHolder;
import com.zf1976.mayi.auth.enhance.JdbcClientDetailsServiceEnhancer;
import com.zf1976.mayi.auth.enhance.MD5PasswordEncoder;
import com.zf1976.mayi.auth.filter.DynamicSecurityFilter;
import com.zf1976.mayi.auth.filter.OAuth2TokenAuthenticationFilter;
import com.zf1976.mayi.auth.filter.SignatureAuthenticationFilter;
import com.zf1976.mayi.auth.filter.provider.DaoAuthenticationEnhancerProvider;
import com.zf1976.mayi.auth.filter.handler.logout.OAuth2LogoutHandler;
import com.zf1976.mayi.auth.filter.handler.logout.Oauth2LogoutSuccessHandler;
import com.zf1976.mayi.auth.service.DynamicDataSourceService;
import com.zf1976.mayi.common.security.property.AuthProperties;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import java.security.KeyPair;


/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthProperties authProperties;
    private final DynamicDataSourceService dynamicDataSourceService;

    public WebSecurityConfiguration(SecurityProperties securityProperties,
                                    UserDetailsService userDetailsService,
                                    AuthProperties authProperties,
                                    DynamicDataSourceService dynamicDataSourceService) {
        this.properties = securityProperties;
        this.userDetailsService = userDetailsService;
        this.authProperties = authProperties;
        this.dynamicDataSourceService = dynamicDataSourceService;
        this.passwordEncoder = new MD5PasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

    @Bean
    @DependsOn(value = "securityProperties")
    @ConditionalOnMissingBean
    public KeyPair keyPair() {
        ClassPathResource classPathResource = new ClassPathResource("root.jks");
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, properties.getRsaSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair("root", properties.getRsaSecret().toCharArray());
    }

    /**
     * 防止覆盖
     * 默认生成 {@link ProviderManager} -> {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider} 默认用户名密码校验
     * 两者效果相同 {@link WebSecurityConfigurerAdapter} authenticationManagerBean() authenticationManager()
     * authenticationManagerBean() 返回实现类 {@link WebSecurityConfigurerAdapter -> AuthenticationManagerDelegator }
     * authenticationManager() 返回实现类 {@link ProviderManager}
     */
    @Bean
    public AuthenticationManager configurableAuthenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * 配置父认证管理器认证管理器配置
     *
     * @param auth auth
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        // 配置增强版DaoAuthenticationEnhancerProvider
        auth.authenticationProvider(new DaoAuthenticationEnhancerProvider(passwordEncoder, userDetailsService));
    }

    /**
     * 安全配置
     *
     * @param http http security
     * @throws Exception throw
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭CSRF
        http.csrf()
            .disable()
            // 允许跨域
            .cors()
            .and()
            // 登出处理
            .logout()
            .logoutUrl("/oauth/logout")
            .addLogoutHandler(new OAuth2LogoutHandler())
            .logoutSuccessHandler(new Oauth2LogoutSuccessHandler())
            .and()
            // 防止iframe跨域
            .headers()
            .frameOptions().disable()
            .and()
            // 关闭会话创建
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
            // 放行OPTIONS请求
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .permitAll()
            // 白名单
            .antMatchers(properties.getIgnoreUri())
            .permitAll()
            // 认证OAuth路径放行
            .antMatchers("/oauth/**").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .addFilterAt(new DynamicSecurityFilter(this.properties, this.dynamicDataSourceService), FilterSecurityInterceptor.class)
            .addFilterBefore(new OAuth2TokenAuthenticationFilter(properties), LogoutFilter.class);
        var jdbcClientDetailsServiceEnhancer = SecurityContextHolder.getShareObject(JdbcClientDetailsServiceEnhancer.class);
        if (this.authProperties.getEnableSignature()) {
            http.addFilterBefore(new SignatureAuthenticationFilter(jdbcClientDetailsServiceEnhancer,
                            "/oauth/**","/**"
                    ), SecurityContextPersistenceFilter.class);
        }
    }

}