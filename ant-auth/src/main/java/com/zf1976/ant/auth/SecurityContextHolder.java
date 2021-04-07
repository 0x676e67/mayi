package com.zf1976.ant.auth;

import com.zf1976.ant.auth.service.DynamicDataSourceService;
import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.common.security.support.session.SessionContextHolder;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.security.pojo.UserDetails;
import com.zf1976.ant.common.security.property.SecurityProperties;
import com.zf1976.ant.common.security.support.signature.standard.AttributeStandards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 只适合单机适用
 *
 * @author mac
 */
@Component
public class SecurityContextHolder extends org.springframework.security.core.context.SecurityContextHolder {

    private static final AntPathMatcher PATH_MATCHER= new AntPathMatcher();
    private static final Map<Class<?>, Object> CONTENTS_MAP = new HashMap<>(16);
    private static final ThreadLocal<Authentication> AUTHENTICATION_THREAD_LOCAL = new ThreadLocal<>() {
        @Override
        protected Authentication initialValue() {
            super.remove();
            return super.initialValue();
        }
    };
    private static UserDetailsService userDetailsService;
    private static DynamicDataSourceService dynamicDataSourceService;
    private static SecurityProperties securityProperties;

    public static UserDetails userDetails(){
        final String username = SessionContextHolder.username();
        LoginUserDetails userDetails = (LoginUserDetails) userDetailsService.loadUserByUsername(username);
        return UserDetails.UserDetailsBuilder.anUserDetails()
                                             .withUserInfo(userDetails.getUserInfo())
                                             .withPermission(new ArrayList<>(userDetails.getPermission()))
                                             .build();
    }

    /**
     * 验证uri
     *
     * @param request request
     * @return /
     */
    public static boolean validateUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Collection<String> allowedUri = dynamicDataSourceService.getAllowUri();
        return allowedUri.stream()
                         .anyMatch(var -> PATH_MATCHER.match(var, uri));
    }

    /**
     * 获取签发方
     *
     * @return /
     */
    public static String getIssuer(){
        return securityProperties.getTokenIssuer();
    }

    /**
     * 构建会话
     *
     * @param token token
     * @return /
     */
    public static Session generatedSession(String token) {
        // 获取用户认证登录细节
        LoginUserDetails userDetails = (LoginUserDetails) SecurityContextHolder.getAuthenticationThreadLocal().getPrincipal();
        Long id = userDetails.getId();
        var request = RequestUtils.getRequest();
        Session session = new Session();
        session.setId(id)
               .setLoginTime(new Date())
               .setUsername(userDetails.getUsername())
               .setOwner(ObjectUtils.nullSafeEquals(userDetails.getUsername(), securityProperties.getOwner()))
               .setIp(RequestUtils.getIpAddress(request))
               .setIpRegion(RequestUtils.getIpRegion(request))
               .setBrowser(RequestUtils.getBrowser(request))
               .setOperatingSystemType(RequestUtils.getOpsSystemType(request))
               .setToken(token);
        session.setAttribute(AttributeStandards.AUTH_DATA_SCOPE, userDetails.getDataScopes());
        return session;
    }

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz){
        return clazz.cast(CONTENTS_MAP.get(clazz));
    }

    public static void setAuthenticationThreadLocal(Authentication authentication) {
        AUTHENTICATION_THREAD_LOCAL.set(authentication);
    }

    public static Authentication getAuthenticationThreadLocal(){
        return AUTHENTICATION_THREAD_LOCAL.get();
    }

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        SecurityContextHolder.securityProperties = securityProperties;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        SecurityContextHolder.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setDynamicDataSourceService(DynamicDataSourceService dynamicDataSourceService) {
        SecurityContextHolder.dynamicDataSourceService = dynamicDataSourceService;
    }

}
