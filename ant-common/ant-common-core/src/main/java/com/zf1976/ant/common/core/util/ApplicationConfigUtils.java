package com.zf1976.ant.common.core.util;

import com.zf1976.ant.common.core.property.CacheProperties;
import com.zf1976.ant.common.core.property.CaptchaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author WINDOWS
 */
@Component
public class ApplicationConfigUtils {

    private static  CaptchaProperties captchaProperties;
    private static  CacheProperties cacheProperties;


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
