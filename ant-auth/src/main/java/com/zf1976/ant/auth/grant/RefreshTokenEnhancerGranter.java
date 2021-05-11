package com.zf1976.ant.auth.grant;

import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.core.util.RequestUtil;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author mac
 * @date 2021/4/9
 */
public class RefreshTokenEnhancerGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "refresh_token";
    private TokenStore tokenStore;
    public RefreshTokenEnhancerGranter(AuthorizationServerTokenServices tokenServices,
                                       ClientDetailsService clientDetailsService,
                                       OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRequestParameters().get("refresh_token");
        final OAuth2Authentication oAuth2Authentication = this.getAuthentication(refreshToken);
        // old access token
        final OAuth2AccessToken oldAccessToken = this.tokenStore.getAccessToken(oAuth2Authentication);
        // 刷新token成功
        OAuth2AccessToken oAuth2AccessToken = this.getTokenServices().refreshAccessToken(refreshToken, tokenRequest);
        if (oAuth2AccessToken != null) {
            // 设置token 创建新会话用到
            RequestUtil.getRequest().setAttribute(HttpHeaders.AUTHORIZATION, oldAccessToken.getValue());
            // 清除当前会话
            SessionManagement.removeSession();
            return oAuth2AccessToken;
        }
        throw new InvalidGrantException("Wrong client for this refresh token: " + refreshToken);
    }

    private OAuth2Authentication getAuthentication(String refreshToken) {
        if (this.tokenStore == null) {
            this.tokenStore = SecurityContextHolder.getShareObject(TokenStore.class);
        }
        return ((RedisTokenStore) this.tokenStore).readAuthenticationForRefreshToken(refreshToken);
    }
}
