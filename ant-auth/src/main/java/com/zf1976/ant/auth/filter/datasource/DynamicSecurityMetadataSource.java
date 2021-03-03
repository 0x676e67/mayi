package com.zf1976.ant.auth.filter.datasource;

import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.auth.DynamicDataSourceService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 动态权限数据源
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private Map<String, Collection<ConfigAttribute>> requestMap;

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
        for (Map.Entry<String, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            if (pathMatcher.match(entry.getKey(), uri)) {
                // 返回匹配URL权限值，自定义数据源
                return Collections.unmodifiableCollection(entry.getValue());
            }
        }
        // 不存在资源资源路径 返回空
        return Collections.emptyList();
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Collection<ConfigAttribute> configAttributes = new CopyOnWriteArraySet<>();
        for (Map.Entry<String, Collection<ConfigAttribute>> collectionEntry : this.requestMap.entrySet()) {
            configAttributes.addAll(collectionEntry.getValue());
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
            this.requestMap = dynamicDataSourceService.loadDataSource();
        }
    }
}
