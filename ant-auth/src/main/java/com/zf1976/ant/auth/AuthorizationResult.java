package com.zf1976.ant.auth;

import com.zf1976.ant.common.security.pojo.Details;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @author mac
 * @date 2021/4/9
 */
public class AuthorizationResult {

    private final OAuth2AccessToken oAuth2AccessToken;
    private final Details details;

    public AuthorizationResult(OAuth2AccessToken oAuth2AccessToken, Details details) {
        this.oAuth2AccessToken = oAuth2AccessToken;
        this.details = details;
    }

    public OAuth2AccessToken getOAuth2AccessToken() {
        return oAuth2AccessToken;
    }

    public Details getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "LoginDetails{" +
                "oAuth2AccessToken=" + oAuth2AccessToken +
                ", details=" + details +
                '}';
    }

}
