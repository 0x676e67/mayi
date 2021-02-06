package com.zf1976.ant.common.encrypt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author mac
 * @date 2021/1/28
 **/
@Component
@ConfigurationProperties(prefix = "api-encrypt")
public class SecretProperties {

    /**
     * 默认开启接口加密/解密
     */
    public static boolean OPEN_ENCRYPT = true;

    /**
     * 默认关闭
     */
    public static boolean SHOW_LOG = false;

    public void setOpen(Boolean open) {
        SecretProperties.OPEN_ENCRYPT = open;
    }

    public void setShowLog(Boolean showLog) {
        SecretProperties.SHOW_LOG = showLog;
    }
}
