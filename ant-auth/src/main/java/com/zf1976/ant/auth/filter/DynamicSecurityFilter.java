package com.zf1976.ant.auth.filter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zf1976.ant.auth.filter.datasource.DynamicSecurityMetadataSource;
import com.zf1976.ant.auth.filter.manager.DynamicAccessDecisionManager;
import com.zf1976.ant.auth.service.impl.DynamicDataSourceService;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 动态权限安全过滤
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicSecurityFilter extends AbstractSecurityInterceptor implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SecurityProperties properties;
    private final DynamicDataSourceService dynamicDataSourceService;

    public DynamicSecurityFilter() {
        this.properties = SpringContextHolder.getBean(SecurityProperties.class);
        this.dynamicDataSourceService = SpringContextHolder.getBean(DynamicDataSourceService.class);
        super.setAccessDecisionManager(new DynamicAccessDecisionManager(dynamicDataSourceService));
        this.checkState();
    }

    public void checkState() {
        Assert.notNull(this.properties, "security config cannot been null");
        Assert.notNull(this.dynamicDataSourceService, "dynamicDataSourceService cannot been null");
        this.init();
    }

    private void init(){
        this.dynamicDataSourceService.getAllowUri();
        this.dynamicDataSourceService.loadDynamicDataSource();
        logger.info("Dynamic data sources rely on initialization！");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest, servletResponse, filterChain);
        //OPTIONS请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            filterInvocation.getChain()
                            .doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            return;
        }
        //白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String uri : this.loadAllowUrl()) {
            if (pathMatcher.match(uri, request.getRequestURI())) {
                filterInvocation.getChain()
                                .doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
                return;
            }
        }
        //调用AccessDecisionManager中的decide方法进行鉴权操作
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain()
                            .doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    private Collection<String> loadAllowUrl() {
        // 配置文件白名单
        Set<String> defaultAllow = Sets.newHashSet(this.properties.getIgnoreUri());
        return Lists.newArrayList(Iterables.concat(this.dynamicDataSourceService.getAllowUri(), defaultAllow));
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }

    public static void main(String[] args) {
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        final Map<String, String> withinPattern = antPathMatcher.extractUriTemplateVariables("/api/{[a-z]}", "/api/7897");
        System.out.println(withinPattern);
        System.out.println(antPathMatcher.match("/api/{api}", "/api/111"));
    }
}
