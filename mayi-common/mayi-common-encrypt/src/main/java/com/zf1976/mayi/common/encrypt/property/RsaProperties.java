package com.zf1976.mayi.common.encrypt.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * rsa加密信息配置 加解密长度有限，需分块加密解密
 *
 * @author ant
 * Create by Ant on 2020/9/10 9:15 下午
 */
@Component
@ConfigurationProperties(prefix = "rsa")
public final class RsaProperties {

    /**
     * RSA公匙
     */
    public static String PRIVATE_KEY;

    /**
     * RSA私匙
     */
    public static String PUBLIC_KEY;

    public void setPublicKey(String publicKey) {
        RsaProperties.PUBLIC_KEY = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        RsaProperties.PRIVATE_KEY = privateKey;
    }
}
