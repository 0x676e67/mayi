package com.zf1976.ant.auth.enhance;

import com.zf1976.ant.common.core.constants.AuthConstants;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Collection;

/**
 * @author mac
 * @date 2021/2/16
 **/
public class RedisTokenStoreEnhancer implements TokenStore{

    private final RedisTokenStore tokenStore;

    public RedisTokenStoreEnhancer(RedisTokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public RedisTokenStoreEnhancer(RedisConnectionFactory connectionFactory) {
        this.tokenStore = new RedisTokenStore(connectionFactory);
    }

    public TokenStore enhance() {
        this.tokenStore.setPrefix(AuthConstants.PROJECT_OAUTH);
        return this.tokenStore;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken oAuth2AccessToken) {
        return this.tokenStore.readAuthentication(oAuth2AccessToken);
    }

    @Override
    public OAuth2Authentication readAuthentication(String tokenValue) {
        return this.tokenStore.readAuthentication(tokenValue);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return this.tokenStore.readAccessToken(tokenValue);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken oAuth2AccessToken) {

    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication oAuth2Authentication) {

    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String s) {
        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        return null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {

    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {

    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication oAuth2Authentication) {
        return this.tokenStore.getAccessToken(oAuth2Authentication);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String username) {
        return this.tokenStore.findTokensByClientIdAndUserName(clientId, username);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String tokenValue) {
        return this.tokenStore.findTokensByClientId(tokenValue);
    }
}
