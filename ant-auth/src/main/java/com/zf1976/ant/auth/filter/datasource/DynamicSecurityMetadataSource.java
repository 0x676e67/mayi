package com.zf1976.ant.auth.filter.datasource;

import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.auth.service.impl.DynamicDataSourceService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * 动态权限数据源
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private Map<String, Collection<String>> requestMap;

    public DynamicSecurityMetadataSource() {
        this.initialize();
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(this.requestMap, "request map cannot been null");
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        Assert.isInstanceOf(FilterInvocation.class, o, "target not is instance of FilterInvocation");
        if (CollectionUtils.isEmpty(requestMap)) {
            this.initialize();
        }
        HttpServletRequest request = ((FilterInvocation) o).getRequest();
        String uri = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 资源URI--Permissions
        for (Map.Entry<String, Collection<String>> entry : requestMap.entrySet()) {
            boolean condition = false;
            // eq匹配
            if (ObjectUtils.nullSafeEquals(entry.getKey(), uri)) {
                condition = true;
                // 失败后进行模式匹配
            } else if (pathMatcher.match(entry.getKey(), uri)) {
                // 返回匹配URL权限值，自定义数据源
                condition = true;
            }
            if (condition) {
                return entry.getValue()
                            .stream()
                            .map(SecurityConfig::new)
                            .collect(Collectors.toUnmodifiableSet());
            }
        }
        // 不存在资源资源路径 返回空
        return Collections.emptyList();
    }

    /**
     * 获取所有权限属性
     *
     * @return {@link Collection<ConfigAttribute>}
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Collection<ConfigAttribute> configAttributes = new CopyOnWriteArraySet<>();
        for (Map.Entry<String, Collection<String>> collectionEntry : this.requestMap.entrySet()) {
            final Set<SecurityConfig> securityConfigs = collectionEntry.getValue()
                                                                       .stream()
                                                                       .map(SecurityConfig::new)
                                                                       .collect(Collectors.toSet());
            configAttributes.addAll(securityConfigs);
        }
        return configAttributes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return DynamicSecurityMetadataSource.class.isAssignableFrom(aClass);
    }

    private void initialize() {
        DynamicDataSourceService dynamicDataSourceService = SpringContextHolder.getBean(DynamicDataSourceService.class);
        Assert.notNull(dynamicDataSourceService, "dynamic datasource cannot been null");
        if (this.requestMap == null) {
            this.requestMap = dynamicDataSourceService.loadDynamicDataSource();
        }
    }
}
