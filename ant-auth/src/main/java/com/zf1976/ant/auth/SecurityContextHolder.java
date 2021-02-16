package com.zf1976.ant.auth;

import com.zf1976.ant.auth.service.DynamicDataSourceService;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mac
 */
public class SecurityContextHolder extends org.springframework.security.core.context.SecurityContextHolder {

    private static final String ANONYMOUS_AUTH = "anonymousUser";
    private static final UserDetailsService USER_DETAILS_SERVICE;
    private static final DynamicDataSourceService DYNAMIC_DATA_SOURCE_SERVICE;
    private static final AntPathMatcher PATH_MATCHER;
    private static final Map<Class<?>, Object> CONTENTS_MAP;
    private static final SecurityProperties SECURITY_PROPERTIES;

    static {
        SECURITY_PROPERTIES = SpringContextHolder.getBean(SecurityProperties.class);
        USER_DETAILS_SERVICE = SpringContextHolder.getBean(UserDetailsService.class);
        DYNAMIC_DATA_SOURCE_SERVICE = SpringContextHolder.getBean(DynamicDataSourceService.class);
        PATH_MATCHER = new AntPathMatcher();
        CONTENTS_MAP = new HashMap<>();
    }

    public static void put(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T get(Class<T> clazz){
        return clazz.cast(CONTENTS_MAP.get(clazz));
    }

    /**
     * 获取当前用户
     *
     * @return userDetails
     */
    public static UserDetails getDetails(){
        final Authentication authentication = getContext().getAuthentication();
        final String username = (String) authentication.getPrincipal();
        return USER_DETAILS_SERVICE.loadUserByUsername(username);
    }

    /**
     * 获取用户id
     *
     * @return id
     */
    public static Long getPrincipalId() {
        String token = ((String) getContext().getAuthentication()
                                             .getCredentials());
        return JwtTokenProvider.getSessionId(token);
    }

    /**
     * 获取主体
     *
     * @return 用户名
     */
    public static String getPrincipal() {
        String token = (String) getContext().getAuthentication()
                                            .getCredentials();
        try {
            return JwtTokenProvider.getClaims(token)
                                   .getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ANONYMOUS_AUTH;
    }

    /**
     * 获取凭证
     *
     * @return token
     */
    public static String getCredentials() {
        return ((String) getContext().getAuthentication()
                                     .getCredentials());
    }

    /**
     * 是否为super admin
     * @return ture / false
     */
    public static boolean isSuperAdmin() {
        return getContext().getAuthentication()
                           .getAuthorities()
                           .stream()
                           .map(GrantedAuthority::getAuthority)
                           .anyMatch(s -> s.equals(ApplicationConfigUtils.getSecurityProperties().getAdmin()));
    }

    /**
     * 校验是否为管理员
     *
     * @param principal 主体/用户名
     * @return /
     */
    public static boolean validateSuperAdmin(String principal) {
        return ObjectUtils.nullSafeEquals(principal, ApplicationConfigUtils.getSecurityProperties().getAdmin());
    }

    /**
     * 获取放行uri
     *
     * @return /
     */
    public static Set<String> getAllowedUri() {
        // 匿名方向uri
        String[] allowUri = ApplicationConfigUtils.getSecurityProperties()
                                                      .getAllowUri();
        Set<String> allow = DYNAMIC_DATA_SOURCE_SERVICE.getAllowUri();
        allow.addAll(Arrays.asList(allowUri));
        return allow;
    }

    public static boolean validateUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Set<String> allowedUri = getAllowedUri();
        return allowedUri.stream()
                         .anyMatch(var -> PATH_MATCHER.match(var, uri));
    }

    public static String getIssuer(){
        return SECURITY_PROPERTIES.getTokenIssuer();
    }
 }
