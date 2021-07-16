package com.zf1976.mayi.upms.biz.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 */
@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    /**
     * 健前缀
     */
    private String keyPrefix;
    /**
     * 字体长度
     */
    private Integer length;
    /**
     * 时间
     */
    private Long expired;
    /**
     * 收件人
     */
    private String name;
    /**
     * 主体
     */
    private String subject;

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public EmailProperties setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public EmailProperties setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Long getExpired() {
        return expired;
    }

    public EmailProperties setExpired(Long expired) {
        this.expired = expired;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmailProperties setName(String name) {
        this.name = name;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailProperties setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public String toString() {
        return "ValidateProperties{" +
                "keyPrefix='" + keyPrefix + '\'' +
                ", length=" + length +
                ", expired=" + expired +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
