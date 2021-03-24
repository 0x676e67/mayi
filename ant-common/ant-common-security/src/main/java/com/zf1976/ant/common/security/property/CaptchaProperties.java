package com.zf1976.ant.common.security.property;

import com.zf1976.ant.common.security.property.enums.CaptchaTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码信息配置
 *
 * @author mac
 * Create by Ant on 2020/9/1 上午11:46
 */
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

    public Long getExpiration() {
        return expiration;
    }

    public CaptchaProperties setExpiration(Long expiration) {
        this.expiration = expiration;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public CaptchaProperties setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public CaptchaProperties setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public CaptchaProperties setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public String getFontName() {
        return fontName;
    }

    public CaptchaProperties setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public CaptchaProperties setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public CaptchaTypeEnum getCodeType() {
        return codeType;
    }

    public CaptchaProperties setCodeType(CaptchaTypeEnum codeType) {
        this.codeType = codeType;
        return this;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public CaptchaProperties setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        return this;
    }

    @Override
    public String toString() {
        return "CaptchaProperties{" +
                "expiration=" + expiration +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", fontName='" + fontName + '\'' +
                ", fontSize=" + fontSize +
                ", codeType=" + codeType +
                ", keyPrefix='" + keyPrefix + '\'' +
                '}';
    }
}
