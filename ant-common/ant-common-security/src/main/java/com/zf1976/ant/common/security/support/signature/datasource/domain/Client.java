package com.zf1976.ant.common.security.support.signature.datasource.domain;

/**
 * @author mac
 * @date 2021/3/24
 **/
public class Client {

    private String clientId;

    private String clientSecret;

    public String getClientId() {
        return clientId;
    }

    public Client setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Client setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                '}';
    }
}
