package com.zf1976.ant.auth;

import com.zf1976.ant.auth.service.impl.DynamicDataSourceService;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.security.pojo.Details;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.common.core.util.RequestUtil;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

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
    private static DynamicDataSourceService dynamicDataSourceService;
    private static SecurityProperties securityProperties;


    public static void createSession(OAuth2AccessToken oAuth2AccessToken) {
        // 获取token
        String tokenValue = oAuth2AccessToken.getValue();
        // 构建session
        Session session = generatedSession(tokenValue);
        // 保存会话
        SessionManagement.storeSession(tokenValue, session);
    }

    @Deprecated
    public static void createSession(String jwtToken) {
        // 构建session
        Session session = generatedSession(jwtToken);
        SessionManagement.storeSession(jwtToken, session);
    }

    /**
     * 构建会话
     *
     * @param token token
     * @return /
     */
    public static Session generatedSession(String token) {
        // 获取用户认证登录细节
        // 当前请求
        HttpServletRequest request = RequestUtil.getRequest();
        Details userDetails = (Details) request.getAttribute(AuthConstants.DETAILS);

        Object expiredTime = request.getAttribute(AuthConstants.SESSION_EXPIRED);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, (Integer) expiredTime);
        Assert.isInstanceOf(Integer.class, expiredTime, "must be an Integer instance");
        Session session = new Session();
        session.setId(userDetails.getUserInfo().getId())
               .setClientId(extractClientId())
               .setLoginTime(new Date())
               .setExpiredTime(instance.getTime())
               .setUsername(userDetails.getUserInfo().getUsername())
               .setOwner(SessionManagement.isOwner(session.getUsername()))
               .setIp(RequestUtil.getIpAddress())
               .setIpRegion(RequestUtil.getIpRegion())
               .setBrowser(RequestUtil.getUserAgent())
               .setOperatingSystemType(RequestUtil.getOpsSystemType())
               .setToken(token);
        return session;
    }

    private static String extractClientId() {
        return ((UserDetails) getContext().getAuthentication()
                                          .getPrincipal()).getUsername();
    }

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz){
        return clazz.cast(CONTENTS_MAP.get(clazz));
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

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        SecurityContextHolder.securityProperties = securityProperties;
    }

    @Autowired
    public void setDynamicDataSourceService(DynamicDataSourceService dynamicDataSourceService) {
        SecurityContextHolder.dynamicDataSourceService = dynamicDataSourceService;
    }

}
