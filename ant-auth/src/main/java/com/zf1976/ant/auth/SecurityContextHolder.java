package com.zf1976.ant.auth;

import com.zf1976.ant.auth.service.DynamicDataSourceService;
import com.zf1976.ant.common.security.pojo.Details;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

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

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz) {
        return clazz.cast(CONTENTS_MAP.get(clazz));
    }

    /**
     * 获取授权成功后细节
     *
     * @return {@link Details}
     */
    public static Details getAuthorizationDetails() {
        LoginUserDetails details = (LoginUserDetails) getContext().getAuthentication()
                                                                  .getDetails();
        return new Details(details.getPermission(), details.getDataPermission(), details.getUser());
    }

    /**
     * 获取签发方
     *
     * @return {@link String}
     */
    public static String getIssuer() {
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
