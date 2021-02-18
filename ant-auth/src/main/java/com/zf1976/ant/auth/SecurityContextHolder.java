package com.zf1976.ant.auth;

import com.zf1976.ant.auth.service.DynamicDataSourceService;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mac
 */
@Component
public class SecurityContextHolder extends org.springframework.security.core.context.SecurityContextHolder {

    private static final String ANONYMOUS_AUTH = "anonymousUser";
    private static final AntPathMatcher PATH_MATCHER= new AntPathMatcher();
    private static final Map<Class<?>, Object> CONTENTS_MAP = new HashMap<>();
    private static KeyPair KEY_PAIR;
    private static UserDetailsService userDetailsService;
    private static DynamicDataSourceService dynamicDataSourceService;
    private static SecurityProperties securityProperties;

    public static KeyPair getKeyPair(){
        return KEY_PAIR;
    }

    public static RSAPublicKey getPublicKey(){
        final KeyPair keyPair = SecurityContextHolder.getKeyPair();
        final PublicKey keyPairPublic = keyPair.getPublic();
        return (RSAPublicKey) keyPairPublic;
    }

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz){
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
        return userDetailsService.loadUserByUsername(username);
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
        Set<String> allow = dynamicDataSourceService.getAllowUri();
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
        return securityProperties.getTokenIssuer();
    }

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        SecurityContextHolder.securityProperties = securityProperties;
        initKeyPair();
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        SecurityContextHolder.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setDynamicDataSourceService(DynamicDataSourceService dynamicDataSourceService) {
        SecurityContextHolder.dynamicDataSourceService = dynamicDataSourceService;
    }

    public void initKeyPair(){
        /*
         * 从classpath下的密钥库中获取密钥对(公钥+私钥)
         */
        if (KEY_PAIR == null) {
            synchronized (SecurityContextHolder.class) {
                if (KEY_PAIR == null) {
                    final char[] secret = securityProperties.getRsaSecret().toCharArray();
                    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("root.jks"), secret);
                    KEY_PAIR = keyStoreKeyFactory.getKeyPair("root", secret);
                }
            }
        }
    }
}
