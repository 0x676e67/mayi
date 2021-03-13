package com.zf1976.ant.common.core.property;

import com.zf1976.ant.common.core.property.enums.CaptchaTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码信息配置
 *
 * @author mac
 * Create by Ant on 2020/9/1 上午11:46
 */
@Data
@Component
@ConfigurationProperties(prefix = "verify-code")
public class CaptchaProperties {

    /**
     * 验证码有效期/毫秒
     */
    private Long expiration = 60000L;

    /**
     * 验证码内容长度
     */
    private Integer length = 2;

    /**
     * 验证码宽度
     */
    private Integer width = 111;

    /**
     * 验证码高度
     */
    private Integer height = 36;

    /**
     * 验证码字体
     */
    private String fontName;

    /**
     * 字体大小
     */
    private Integer fontSize = 25;

    /**
     * 验证码配置 验证码类型
     */
    private CaptchaTypeEnum codeType;

    /**
     * 验证码 key
     */
    private String keyPrefix;

}
