package com.zf1976.mayi.upms.biz.security.config;

import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.security.filter.DynamicSecurityFilter;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.apache.http.client.methods.HttpOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * @author mac
 * @date 2021/6/9
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwt-check-uri}")
    private String jwtCheckUri;

    @Value("${spring.security.oauth2.resourceserver.resource-id}")
    private String resourceId;

    private final DynamicDataSourceService dynamicDataSourceService;
    private final SecurityProperties securityProperties;

    public OAuth2ResourceServerConfiguration(OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
                                             DynamicDataSourceService dynamicDataSourceService,
                                             SecurityProperties securityProperties) {
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
        this.dynamicDataSourceService = dynamicDataSourceService;
        this.securityProperties = securityProperties;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        // 资源服务器ID
        resources.resourceId(resourceId);
        // 远程校验token服务
        resources.tokenServices(this.remoteTokenServices());
        // 无状态
        resources.stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        // 拦截所有请求
        http.authorizeRequests()
            // OPTIONS放行
            .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
            .antMatchers(this.securityProperties.getIgnoreUri()).permitAll()
            .anyRequest()
            .authenticated();


        // 关闭CSRF和允许跨域
        http.csrf().disable()
            .cors();

        // 关闭HttpBasic认证
        http.httpBasic().disable();

        // 动态权限过滤
        http.addFilterAt(new DynamicSecurityFilter(this.securityProperties, this.dynamicDataSourceService), FilterSecurityInterceptor.class);
    }

    /**
     * 远程访问认证服务器校验token
     *
     * @return {@link RemoteTokenServices}
     */
    private RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(this.jwtCheckUri);
        remoteTokenServices.setClientId(this.clientId);
        remoteTokenServices.setClientSecret(this.clientSecret);
        return remoteTokenServices;
    }

    private OAuth2ResourceServerProperties.Jwt getJwt() {
        return this.oAuth2ResourceServerProperties.getJwt();
    }

}
