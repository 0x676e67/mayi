package com.zf1976.ant.auth.config;

import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.auth.filter.Oauth2TokenAuthenticationFilter;
import com.zf1976.ant.auth.filter.provider.DaoAuthenticationEnhancerProvider;
import com.zf1976.ant.auth.handler.logout.Oauth2LogoutHandler;
import com.zf1976.ant.auth.handler.logout.Oauth2LogoutSuccessHandler;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.security.KeyPair;


/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public WebSecurityConfigurer(SecurityProperties securityProperties,
                                 UserDetailsService userDetailsService) {
        this.securityProperties = securityProperties;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

    @Bean
    @DependsOn(value = "securityProperties")
    public KeyPair keyPair() {
        return SecurityContextHolder.getKeyPair();
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
        DaoAuthenticationEnhancerProvider authenticationEnhancerProvider = DaoAuthenticationEnhancerProvider.builder()
                                                                                   .setPasswordEncoder(this.passwordEncoder)
                                                                                   .setUserDetailsService(this.userDetailsService)
                                                                                   .build();
        auth.authenticationProvider(authenticationEnhancerProvider);
        // auth.parentAuthenticationManager(providerManager);
        // ProviderManager providerManager = new ProviderManager(Collections.singletonList(authenticationEnhancerProvider));
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
            .addLogoutHandler(new Oauth2LogoutHandler())
            .logoutSuccessHandler(new Oauth2LogoutSuccessHandler())
            .and()
            // 防止iframe跨域
            .headers()
            .frameOptions()
            .disable()
            .and()
            // 关闭会话创建
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .requestMatchers(EndpointRequest.toAnyEndpoint())
            .permitAll()
            // 放行OPTIONS请求
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .permitAll()
            // 白名单
            .antMatchers(securityProperties.getAllowUri())
            .permitAll()
            .antMatchers("/oauth/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .addFilterBefore(new Oauth2TokenAuthenticationFilter(), LogoutFilter.class);
    }
}
