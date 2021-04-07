package com.zf1976.ant.auth.service;

import org.springframework.security.access.ConfigAttribute;

/**
 * @author mac
 * @date 2021/4/7
 */
public class AuthConfigAttribute implements ConfigAttribute {

    private String attribute;

    public AuthConfigAttribute() {
    }

    public AuthConfigAttribute(String attribute) {
        this.attribute = attribute;
    }

    public AuthConfigAttribute setAttribute(String attribute) {
        this.attribute = attribute;
        return this;
    }

    @Override
    public String getAttribute() {
        return this.attribute;
    }

    @Override
    public String toString() {
        return "AuthConfigAttribute{" +
                "attribute='" + attribute + '\'' +
                '}';
    }
}
