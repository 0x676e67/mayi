package com.zf1976.ant.common.core.util;

import com.zf1976.ant.common.core.dev.CacheProperties;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author WINDOWS
 */
@Component
public class ApplicationConfigUtils {

    private static  SecurityProperties securityProperties;
    private static  CaptchaProperties captchaProperties;
    private static  CacheProperties cacheProperties;

    public static SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        ApplicationConfigUtils.securityProperties = securityProperties;
    }

    public static CaptchaProperties getCaptchaProperties() {
        return captchaProperties;
    }

    @Autowired
    public void setCaptchaProperties(CaptchaProperties captchaProperties) {
        ApplicationConfigUtils.captchaProperties = captchaProperties;
    }

    public static CacheProperties getCacheProperties() {
        return cacheProperties;
    }

    @Autowired
    public void setCacheProperties(CacheProperties cacheProperties) {
        ApplicationConfigUtils.cacheProperties = cacheProperties;
    }
}
