package com.zf1976.ant.common.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 */
@Data
@Component
@ConfigurationProperties(prefix = "validate")
public class ValidateProperties {

    private String keyPrefix;

    private Integer length;

    private Long expired;

    private String name;

    private String subject;

}
