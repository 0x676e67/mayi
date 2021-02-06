package com.zf1976.ant.common.core.util;

import com.zf1976.ant.common.core.dev.CacheProperties;
import com.zf1976.ant.common.core.dev.CaptchaProperties;
import com.zf1976.ant.common.core.dev.SecurityProperties;

/**
 * @author WINDOWS
 */
public class ApplicationConfigUtils {

    private static final SecurityProperties SECURITY_PROPERTIES = SpringContextHolder.getBean(SecurityProperties.class);
    private static final CaptchaProperties CAPTCHA_PROPERTIES = SpringContextHolder.getBean(CaptchaProperties.class);
    private static final CacheProperties CACHE_PROPERTIES = SpringContextHolder.getBean(CacheProperties.class);

    public static SecurityProperties getSecurityProperties() { return SECURITY_PROPERTIES; }

    public static CaptchaProperties getCaptchaProperties() {
        return CAPTCHA_PROPERTIES;
    }

    public static CacheProperties getCacheProperties() {
        return CACHE_PROPERTIES;
    }
}
