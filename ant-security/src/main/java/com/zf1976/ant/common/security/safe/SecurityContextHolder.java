package com.zf1976.ant.common.security.safe;

import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.security.safe.service.DynamicDataSourceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;

/**
 * @author mac
 */
public class SecurityContextHolder extends org.springframework.security.core.context.SecurityContextHolder {

    private static final String ANONYMOUS_AUTH = "anonymousUser";
    private static final UserDetailsService USER_DETAILS_SERVICE;
    private static final  DynamicDataSourceService DYNAMIC_DATA_SOURCE_SERVICE;
    private static final AntPathMatcher PATH_MATCHER;

    static {
        USER_DETAILS_SERVICE = SpringContextHolder.getBean(UserDetailsService.class);
        DYNAMIC_DATA_SOURCE_SERVICE = SpringContextHolder.getBean(DynamicDataSourceService.class);
        PATH_MATCHER = new AntPathMatcher();
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
                         .anyMatch(var -> {
                             return PATH_MATCHER.match(var, uri);
                         });
    }
 }
