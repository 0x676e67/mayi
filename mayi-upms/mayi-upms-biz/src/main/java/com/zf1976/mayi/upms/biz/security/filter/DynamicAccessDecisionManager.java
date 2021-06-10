package com.zf1976.mayi.upms.biz.security.filter;

import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
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
        // 资源所有者放行
        if (SessionManagement.isOwner()) {
            return;
        }
        // 过滤调用
        FilterInvocation filterInvocation = (FilterInvocation) target;
        // 请求
        HttpServletRequest request = filterInvocation.getRequest();
        // 请求URI
        String uri = request.getRequestURI();
        // 请求方法
        String method = request.getMethod();
        // URI-Method
        Map<String, String> methodMap = this.dynamicDataSourceService.getResourceMethodMap();
        // 条件
        boolean condition = false;
        for (Map.Entry<String, String> entry : methodMap.entrySet()) {
            // eq匹配
            if (ObjectUtils.nullSafeEquals(entry.getKey(), uri) && ObjectUtils.nullSafeEquals(entry.getValue(), method)) {
                condition = true;
                break;
                // 失败后进行模式匹配
            } else if (pathMatcher.match(entry.getKey(), uri) && ObjectUtils.nullSafeEquals(method, entry.getValue())) {
                condition = true;
                break;
            }
        }
        if (!condition) {
            throw new AccessDeniedException("You do not have permission to access, please contact the administrator");
        }
        // 用户所有权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 所需要权限
        Set<String> needPermission = collection.stream()
                                               .map(ConfigAttribute::getAttribute)
                                               .collect(Collectors.toSet());
        // 所需权限为空，当即放行
        if (CollectionUtils.isEmpty(needPermission)) {
            return;
        }
        condition = authorities.stream()
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
