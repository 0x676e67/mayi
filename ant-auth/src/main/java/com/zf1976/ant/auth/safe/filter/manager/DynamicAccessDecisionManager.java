package com.zf1976.ant.auth.safe.filter.manager;

import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.auth.service.DynamicDataSourceService;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态访问决策管理器
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicAccessDecisionManager implements AccessDecisionManager {

    private final DynamicDataSourceService dynamicDataSourceService;
    private final AntPathMatcher pathMatcher;

    public DynamicAccessDecisionManager(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public void decide(Authentication authentication, Object target, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        if (SecurityContextHolder.isSuperAdmin()) {
            return;
        }
        // 过滤调用
        FilterInvocation filterInvocation = (FilterInvocation) target;
        // 请求
        HttpServletRequest request = filterInvocation.getRequest();
        // 请求uri
        String uri = request.getRequestURI();
        // 请求方法
        String method = request.getMethod();
        Map<String, String> methodMap = this.dynamicDataSourceService.getMatcherMethodMap();
        boolean val = false;
        for (Map.Entry<String, String> entry : methodMap.entrySet()) {
            if (pathMatcher.match(entry.getKey(), uri) && ObjectUtils.nullSafeEquals(method,entry.getValue())) {
                val = true;
                break;
            }
        }
        if (!val) {
            throw new AccessDeniedException("You do not have permission to access, please contact the administrator");
        }
        // 用户所有权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 所需要权限
        Set<String> needPermission = collection.stream()
                                        .map(ConfigAttribute::getAttribute)
                                        .collect(Collectors.toSet());

        boolean condition = authorities.stream()
                               .map(GrantedAuthority::getAuthority)
                               .collect(Collectors.toSet())
                               .containsAll(needPermission);
        if (!condition) {
            throw new AccessDeniedException("You do not have permission to access, please contact the administrator");
        }
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return DynamicAccessDecisionManager.class.isAssignableFrom(aClass);
    }
}
