package com.zf1976.ant.auth.enhance;

import com.zf1976.ant.auth.AuthorizationConstants;
import com.zf1976.ant.auth.LoginUserDetails;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.auth.filter.manager.SessionContextHolder;
import com.zf1976.ant.common.core.util.RequestUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mac
 * @date 2021/2/16
 **/
public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        Map<String, Object> additionalInformation = new LinkedHashMap<>();
        // 签发方
        additionalInformation.put(AuthorizationConstants.ISSUER, SecurityContextHolder.getIssuer());
        // 签发时间
        additionalInformation.put(AuthorizationConstants.IAT, System.currentTimeMillis() / 1000);
        // token唯一标识
        additionalInformation.put(AuthorizationConstants.JTI, oAuth2AccessToken.getValue());
        // 用户id
        additionalInformation.put(AuthorizationConstants.JWT_USER_ID_KEY, this.getId(oAuth2Authentication));
        // 客户端id
        additionalInformation.put(AuthorizationConstants.JWT_CLIENT_ID_KEY, oAuth2Request.getClientId());
        // 设置自定义information
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInformation);
        // 保存会话状态
        this.saveSessionState(oAuth2AccessToken, oAuth2Authentication);
        return oAuth2AccessToken;
    }

    public void saveSessionState(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        // 获取token
        String tokenValue = oAuth2AccessToken.getValue();
        // 获取用户认证登录细节
        LoginUserDetails userDetails = (LoginUserDetails) oAuth2Authentication.getPrincipal();
        // 获取token过期时间
        Integer expiration = oAuth2AccessToken.getExpiresIn();
        // 设置token过期时间
        RequestUtils.getRequest()
                    .setAttribute(AuthorizationConstants.EXPIRED, expiration);
        // 保存会话
        SessionContextHolder.set(tokenValue, userDetails);
    }

    public Long getId(OAuth2Authentication oAuth2Authentication) {
        LoginUserDetails loginUserDetails = (LoginUserDetails) oAuth2Authentication.getPrincipal();
        return loginUserDetails.getId();
    }
}
