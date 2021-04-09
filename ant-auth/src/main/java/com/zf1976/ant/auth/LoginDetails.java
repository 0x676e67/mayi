package com.zf1976.ant.auth;

import com.zf1976.ant.common.security.pojo.Details;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * @author mac
 * @date 2021/4/9
 */
public class LoginDetails {

    private OAuth2AccessToken oAuth2AccessToken;

    private Details details;

    public OAuth2AccessToken getOAuth2AccessToken() {
        return oAuth2AccessToken;
    }

    public LoginDetails setOAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
        return this;
    }

    public Details getDetails() {
        return details;
    }

    public LoginDetails setDetails(Details details) {
        this.details = details;
        return this;
    }

    @Override
    public String toString() {
        return "LoginDetails{" +
                "oAuth2AccessToken=" + oAuth2AccessToken +
                ", details=" + details +
                '}';
    }


    public static final class LoginDetailsBuilder {
        private OAuth2AccessToken oAuth2AccessToken;
        private Details details;

        private LoginDetailsBuilder() {
        }

        public static LoginDetailsBuilder builder() {
            return new LoginDetailsBuilder();
        }

        public LoginDetailsBuilder oAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
            this.oAuth2AccessToken = oAuth2AccessToken;
            return this;
        }

        public LoginDetailsBuilder details(Details details) {
            this.details = details;
            return this;
        }

        public LoginDetails build() {
            LoginDetails loginDetails = new LoginDetails();
            loginDetails.setOAuth2AccessToken(oAuth2AccessToken);
            loginDetails.setDetails(details);
            return loginDetails;
        }
    }
}
