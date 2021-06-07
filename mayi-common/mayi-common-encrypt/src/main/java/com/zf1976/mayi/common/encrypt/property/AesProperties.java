package com.zf1976.mayi.common.encrypt.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/1/3
 **/
@Component
@ConfigurationProperties(prefix = "aes")
public class AesProperties {

    /**
     * AES 密钥
     */
    public static String KEY;

    public static String IV;

    public void setIv(String iv) { IV = iv; }

    public void setKey(String key) {
        KEY = key;
    }
}
