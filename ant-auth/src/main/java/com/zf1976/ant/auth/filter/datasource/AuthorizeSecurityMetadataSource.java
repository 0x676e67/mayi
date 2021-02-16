package com.zf1976.ant.auth.filter.datasource;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

/**
 * @author mac
 * @date 2021/1/1
 **/
public class AuthorizeSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private Map<String, Collection<ConfigAttribute>> requestMap;

    public AuthorizeSecurityMetadataSource() {
        this.checkState();
    }
    private void checkState() {
        Assert.notNull(this.requestMap, "request map cannot been null");
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        Assert.isInstanceOf(FilterInvocation.class, o, "target not is instance of FilterInvocation");
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AuthorizeSecurityMetadataSource.class.isAssignableFrom(aClass);
    }
}
