package com.zf1976.ant.common.security.config;

import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.security.safe.filter.DynamicSecurityFilter;
import com.zf1976.ant.common.security.safe.filter.SecurityAuthenticationProcessingFilter;
import com.zf1976.ant.common.security.safe.filter.SecurityJwtAuthenticationFilter;
import com.zf1976.ant.common.security.safe.filter.SignatureAuthenticationFilter;
import com.zf1976.ant.common.security.safe.handler.access.SecurityAccessDeniedHandler;
import com.zf1976.ant.common.security.safe.handler.access.SecurityAuthenticationEntryPoint;
import com.zf1976.ant.common.security.safe.handler.logout.SecurityLogoutHandler;
import com.zf1976.ant.common.security.safe.handler.logout.SecurityLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author mac
 * Create by Ant on 2020/9/1 上午11:32
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProperties securityProperties;

    public SecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭CSRF
        http.csrf().disable()
            // 允许跨域
            .cors()
            .and()
            // 登出处理
            .logout()
            .logoutUrl("/auth/logout")
            .addLogoutHandler(new SecurityLogoutHandler())
            .logoutSuccessHandler(new SecurityLogoutSuccessHandler())
            .and()
            .exceptionHandling()
            .accessDeniedHandler(new SecurityAccessDeniedHandler())
            .authenticationEntryPoint(new SecurityAuthenticationEntryPoint())
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
            // 放行OPTIONS请求
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 白名单
            .antMatchers(securityProperties.getAllowUri()).permitAll()
            .anyRequest()
            .authenticated()
            .and()
            // 动态数据源过滤
            .addFilterBefore(new DynamicSecurityFilter(), FilterSecurityInterceptor.class)
            // 用户名密码验证
            .addFilterAt(new SecurityAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            // 签名过滤
            .addFilterBefore(new SignatureAuthenticationFilter(), SecurityAuthenticationProcessingFilter.class)
            // Jwt token过滤
            .addFilterAfter(new SecurityJwtAuthenticationFilter(), SecurityAuthenticationProcessingFilter.class);
    }
}
