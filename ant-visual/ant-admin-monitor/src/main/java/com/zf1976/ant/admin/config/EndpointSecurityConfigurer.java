package com.zf1976.ant.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

/**
 * @author mac
 * @date 2021/5/25
 */
@Configuration
@EnableWebSecurity
public class EndpointSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final AdminServerProperties adminServer;

    /**
     * Instantiates a new Security secure config.
     *
     * @param adminServer the admin server
     */
    public EndpointSecurityConfigurer(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        final String adminServerContextPath = this.adminServer.getContextPath();
        successHandler.setDefaultTargetUrl(adminServerContextPath +"/");

        http.authorizeRequests()
            .antMatchers(adminServerContextPath +  "/assets/**").permitAll()
            .antMatchers(adminServerContextPath

                    + "/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage(adminServerContextPath  + "/login").successHandler(successHandler).and()
            .logout().logoutUrl(adminServerContextPath +  "/logout").and()
            .httpBasic().and()
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers(
                    new AntPathRequestMatcher(adminServerContextPath +  "/instances", HttpMethod.POST.toString()),
                    new AntPathRequestMatcher(adminServerContextPath +  "/instances/*", HttpMethod.DELETE.toString()),
                    new AntPathRequestMatcher(adminServerContextPath  + "/actuator/**")
            )
            .and()
            .rememberMe().key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600);

    }
}
