package com.zf1976.mayi.auth.enhance;

import com.zf1976.mayi.auth.LoginUserDetails;
import com.zf1976.mayi.auth.SecurityContextHolder;
import com.zf1976.mayi.common.core.constants.AuthConstants;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mac
 * @date 2021/2/16
 **/
@SuppressWarnings("all")
public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        LoginUserDetails loginDetails = (LoginUserDetails) oAuth2Authentication.getPrincipal();
        Integer expiredIn = oAuth2AccessToken.getExpiresIn();
        Map<String, Object> additionalInformation = new LinkedHashMap<>();
        // 签发方
        additionalInformation.put(AuthConstants.ISSUER, SecurityContextHolder.getIssuer());
        // 签发时间 如果时间为new Date(), 网关拿公钥 解析错误:An error occurred while trying to decode Jwt: expiresAt must be after issueAt
        additionalInformation.put(AuthConstants.IAT, System.currentTimeMillis() / 1000);
        // token唯一标识
        additionalInformation.put(AuthConstants.JTI, oAuth2AccessToken.getValue());
        // 客户端id
        additionalInformation.put(AuthConstants.JWT_CLIENT_ID_KEY, oAuth2Request.getClientId());
        // 用户名
        additionalInformation.put(AuthConstants.USERNAME, loginDetails.getUsername());
        // grant类型
        additionalInformation.put(AuthConstants.GRANT_TYPE, oAuth2Request.getGrantType());
        // 设置自定义information
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInformation);
        return oAuth2AccessToken;
    }

}
