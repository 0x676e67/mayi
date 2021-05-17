package com.zf1976.ant.auth;

import com.zf1976.ant.auth.service.impl.DynamicDataSourceService;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.security.pojo.Details;
import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
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

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz){
        return clazz.cast(CONTENTS_MAP.get(clazz));
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
