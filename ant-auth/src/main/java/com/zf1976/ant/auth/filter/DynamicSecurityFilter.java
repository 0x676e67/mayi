package com.zf1976.ant.auth.filter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zf1976.ant.auth.filter.datasource.DynamicSecurityMetadataSource;
import com.zf1976.ant.auth.filter.manager.DynamicAccessDecisionManager;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.security.DynamicDataSourceService;
import com.zf1976.ant.common.core.property.SecurityProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 动态权限安全过滤
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicSecurityFilter extends AbstractSecurityInterceptor implements Filter {

    private final SecurityProperties securityConfig;
    private final DynamicDataSourceService dynamicDataSourceService;

    public DynamicSecurityFilter() {
        this.securityConfig = SpringContextHolder.getBean(SecurityProperties.class);
        this.dynamicDataSourceService = SpringContextHolder.getBean(DynamicDataSourceService.class);
        super.setAccessDecisionManager(new DynamicAccessDecisionManager(dynamicDataSourceService));
        this.checkState();
    }

    public void checkState() {
        Assert.notNull(this.securityConfig, "security config cannot been null");
        Assert.notNull(this.dynamicDataSourceService, "dynamicDataSourceService cannot been null");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain);
        //OPTIONS请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
            return;
        }
        //白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String path : this.loadAllowUrl()) {
            if (pathMatcher.match(path, request.getRequestURI())) {
                fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
                return;
            }
        }
        //此处会调用AccessDecisionManager中的decide方法进行鉴权操作
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    private Collection<String> loadAllowUrl() {
        // 配置文件白名单
        Set<String> dynamicAllow = this.dynamicDataSourceService.getAllowUri();
        Set<String> defaultAllow = Stream.of(this.securityConfig.getIgnoreUri())
                                           .collect(Collectors.toSet());
        return Lists.newArrayList(Iterables.concat(dynamicAllow, defaultAllow));
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }

}
