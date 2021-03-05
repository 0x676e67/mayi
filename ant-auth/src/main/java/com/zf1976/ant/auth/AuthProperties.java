package com.zf1976.ant.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/3/5
 **/
@Data
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

}
