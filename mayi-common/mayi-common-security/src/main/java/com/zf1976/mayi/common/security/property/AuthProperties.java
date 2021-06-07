package com.zf1976.mayi.common.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/3/5
 **/
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * 开启接口方法签名 默认关闭
     */
    private Boolean enableSignature = Boolean.FALSE;

    /**
     * 单服务模式｜网关模式 -> false | true
     */
    private Boolean model = Boolean.FALSE;

    public Boolean getEnableSignature() {
        return enableSignature;
    }

    public AuthProperties setEnableSignature(Boolean enableSignature) {
        this.enableSignature = enableSignature;
        return this;
    }

    public Boolean getModel() {
        return model;
    }

    public AuthProperties setModel(Boolean model) {
        this.model = model;
        return this;
    }

    @Override
    public String toString() {
        return "AuthProperties{" +
                "enableSignature=" + enableSignature +
                ", model=" + model +
                '}';
    }
}
