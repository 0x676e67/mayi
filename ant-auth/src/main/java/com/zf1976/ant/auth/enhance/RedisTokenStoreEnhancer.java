package com.zf1976.ant.auth.enhance;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author mac
 * @date 2021/2/16
 **/
public class RedisTokenStoreEnhancer implements TokenStore{

    private static final boolean springDataRedis_2_0 = ClassUtils.isPresent("org.springframework.data.redis.connection.RedisStandaloneConfiguration", RedisTokenStore.class.getClassLoader());
    private final RedisConnectionFactory connectionFactory;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private String prefix = "";
    private Method redisConnectionSet_2_0;

    public RedisTokenStoreEnhancer(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        if (springDataRedis_2_0) {
            this.loadRedisConnectionMethods_2_0();
        }

    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private void loadRedisConnectionMethods_2_0() {
        this.redisConnectionSet_2_0 = ReflectionUtils.findMethod(RedisConnection.class, "set", byte[].class, byte[].class);
    }

    private RedisConnection getConnection() {
        return this.connectionFactory.getConnection();
    }

    private byte[] serialize(Object object) {
        return this.serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return this.serialize(this.prefix + object);
    }

    private OAuth2AccessToken deserializeAccessToken(byte[] bytes) {
        return this.serializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return this.serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
        return this.serializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
    }

    private byte[] serialize(String string) {
        return this.serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return this.serializationStrategy.deserializeString(bytes);
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = this.authenticationKeyGenerator.extractKey(authentication);
        byte[] serializedKey = this.serializeKey("auth_to_access:" + key);
        byte[] bytes;

        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(serializedKey);
        }

        OAuth2AccessToken accessToken = this.deserializeAccessToken(bytes);
        if (accessToken != null) {
            OAuth2Authentication storedAuthentication = this.readAuthentication(accessToken.getValue());
            if (storedAuthentication == null || !key.equals(this.authenticationKeyGenerator.extractKey(storedAuthentication))) {
                this.storeAccessToken(accessToken, authentication);
            }
        }

        return accessToken;
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return this.readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        RedisConnection conn = this.getConnection();

        byte[] bytes;
        try {
            bytes = conn.get(this.serializeKey("auth:" + token));
        } finally {
            conn.close();
        }

        return this.deserializeAuthentication(bytes);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return this.readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        RedisConnection conn = this.getConnection();

        OAuth2Authentication var5;
        try {
            byte[] bytes = conn.get(this.serializeKey("refresh_auth:" + token));
            var5 = this.deserializeAuthentication(bytes);
        } finally {
            conn.close();
        }

        return var5;
    }

    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        byte[] serializedAccessToken = this.serialize(token);
        byte[] serializedAuth = this.serialize(authentication);
        byte[] accessKey = this.serializeKey("access:" + token.getValue());
        byte[] authKey = this.serializeKey("auth:" + token.getValue());
        byte[] authToAccessKey = this.serializeKey("auth_to_access:" + this.authenticationKeyGenerator.extractKey(authentication));
        byte[] approvalKey = this.serializeKey("uname_to_access:" + getApprovalKey(authentication));
        byte[] clientId = this.serializeKey("client_id_to_access:" + authentication.getOAuth2Request().getClientId());

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, accessKey, serializedAccessToken);
                    this.redisConnectionSet_2_0.invoke(conn, authKey, serializedAuth);
                    this.redisConnectionSet_2_0.invoke(conn, authToAccessKey, serializedAccessToken);
                } catch (Exception var24) {
                    throw new RuntimeException(var24);
                }
            } else {
                conn.set(accessKey, serializedAccessToken);
                conn.set(authKey, serializedAuth);
                conn.set(authToAccessKey, serializedAccessToken);
            }

            if (!authentication.isClientOnly()) {
                conn.sAdd(approvalKey, new byte[][]{serializedAccessToken});
            }

            conn.sAdd(clientId, new byte[][]{serializedAccessToken});
            if (token.getExpiration() != null) {
                int seconds = token.getExpiresIn();
                conn.expire(accessKey, seconds);
                conn.expire(authKey, seconds);
                conn.expire(authToAccessKey, seconds);
                conn.expire(clientId, seconds);
                conn.expire(approvalKey, seconds);
            }

            OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null && refreshToken.getValue() != null) {
                byte[] refresh = this.serialize(token.getRefreshToken()
                                                     .getValue());
                byte[] auth = this.serialize(token.getValue());
                byte[] refreshToAccessKey = this.serializeKey("refresh_to_access:" + token.getRefreshToken()
                                                                                          .getValue());
                byte[] accessToRefreshKey = this.serializeKey("access_to_refresh:" + token.getValue());
                if (springDataRedis_2_0) {
                    try {
                        this.redisConnectionSet_2_0.invoke(conn, refreshToAccessKey, auth);
                        this.redisConnectionSet_2_0.invoke(conn, accessToRefreshKey, refresh);
                    } catch (Exception var23) {
                        throw new RuntimeException(var23);
                    }
                } else {
                    conn.set(refreshToAccessKey, auth);
                    conn.set(accessToRefreshKey, refresh);
                }

                refreshTokenInstanceofCheck(conn, refreshToken, refreshToAccessKey, accessToRefreshKey);
            }

            conn.closePipeline();
        }

    }

    private void refreshTokenInstanceofCheck(RedisConnection conn,
                                             OAuth2RefreshToken refreshToken,
                                             byte[] refreshToAccessKey, byte[] accessToRefreshKey) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                                  .intValue();
                conn.expire(refreshToAccessKey, seconds);
                conn.expire(accessToRefreshKey, seconds);
            }
        }
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? "" : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    public void removeAccessToken(OAuth2AccessToken accessToken) {
        this.removeAccessToken(accessToken.getValue());
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = this.serializeKey("access:" + tokenValue);
        byte[] bytes;

        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(key);
        }

        return this.deserializeAccessToken(bytes);
    }

    public void removeAccessToken(String tokenValue) {
        byte[] accessKey = this.serializeKey("access:" + tokenValue);
        byte[] authKey = this.serializeKey("auth:" + tokenValue);
        byte[] accessToRefreshKey = this.serializeKey("access_to_refresh:" + tokenValue);

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            conn.get(accessKey);
            conn.get(authKey);
            conn.del(new byte[][]{accessKey});
            conn.del(new byte[][]{accessToRefreshKey});
            conn.del(new byte[][]{authKey});
            List<Object> results = conn.closePipeline();
            byte[] access = (byte[]) results.get(0);
            byte[] auth = (byte[]) results.get(1);
            OAuth2Authentication authentication = this.deserializeAuthentication(auth);
            if (authentication != null) {
                String key = this.authenticationKeyGenerator.extractKey(authentication);
                byte[] authToAccessKey = this.serializeKey("auth_to_access:" + key);
                byte[] unameKey = this.serializeKey("uname_to_access:" + getApprovalKey(authentication));
                byte[] clientId = this.serializeKey("client_id_to_access:" + authentication.getOAuth2Request()
                                                                                           .getClientId());
                conn.openPipeline();
                conn.del(new byte[][]{authToAccessKey});
                conn.sRem(unameKey, new byte[][]{access});
                conn.sRem(clientId, new byte[][]{access});
                conn.del(new byte[][]{this.serialize("access:" + key)});
                conn.closePipeline();
            }
        }

    }

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        byte[] refreshKey = this.serializeKey("refresh:" + refreshToken.getValue());
        byte[] refreshAuthKey = this.serializeKey("refresh_auth:" + refreshToken.getValue());
        byte[] serializedRefreshToken = this.serialize(refreshToken);

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, refreshKey, serializedRefreshToken);
                    this.redisConnectionSet_2_0.invoke(conn, refreshAuthKey, this.serialize(authentication));
                } catch (Exception var13) {
                    throw new RuntimeException(var13);
                }
            } else {
                conn.set(refreshKey, serializedRefreshToken);
                conn.set(refreshAuthKey, this.serialize(authentication));
            }

            refreshTokenInstanceofCheck(conn, refreshToken, refreshKey, refreshAuthKey);

            conn.closePipeline();
        }

    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = this.serializeKey("refresh:" + tokenValue);
        byte[] bytes;


        try (RedisConnection conn = this.getConnection()) {
            bytes = conn.get(key);
        }

        return this.deserializeRefreshToken(bytes);
    }

    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        this.removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = this.serializeKey("refresh:" + tokenValue);
        byte[] refreshAuthKey = this.serializeKey("refresh_auth:" + tokenValue);
        byte[] refresh2AccessKey = this.serializeKey("refresh_to_access:" + tokenValue);
        byte[] access2RefreshKey = this.serializeKey("access_to_refresh:" + tokenValue);

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            conn.del(new byte[][]{refreshKey});
            conn.del(new byte[][]{refreshAuthKey});
            conn.del(new byte[][]{refresh2AccessKey});
            conn.del(new byte[][]{access2RefreshKey});
            conn.closePipeline();
        }

    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        this.removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {
        byte[] key = this.serializeKey("refresh_to_access:" + refreshToken);
        List<Object> results;

        try (RedisConnection conn = this.getConnection()) {
            conn.openPipeline();
            conn.get(key);
            conn.del(new byte[][]{key});
            results = conn.closePipeline();
        }

        byte[] bytes = (byte[]) results.get(0);
        String accessToken = this.deserializeString(bytes);
        if (accessToken != null) {
            this.removeAccessToken(accessToken);
        }

    }

    private List<byte[]> getByteLists(byte[] approvalKey, RedisConnection conn) {
        Long size = conn.sCard(approvalKey);
        assert size != null;
        List<byte[]> byteList = new ArrayList<>(size.intValue());
        Cursor<byte[]> cursor = conn.sScan(approvalKey, ScanOptions.NONE);

        while(cursor.hasNext()) {
            byteList.add(cursor.next());
        }

        return byteList;
    }

    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        byte[] approvalKey = this.serializeKey("uname_to_access:" + getApprovalKey(clientId, userName));
        return getOAuth2AccessTokenList(approvalKey);
    }

    private Collection<OAuth2AccessToken> getOAuth2AccessTokenList(byte[] approvalKey) {
        List<byte[]> byteList;

        try (RedisConnection conn = this.getConnection()) {
            byteList = this.getByteLists(approvalKey, conn);
        }

        if (byteList.size() != 0) {
            List<OAuth2AccessToken> accessTokens = new ArrayList<>(byteList.size());

            for (byte[] bytes : byteList) {
                OAuth2AccessToken accessToken = this.deserializeAccessToken(bytes);
                accessTokens.add(accessToken);
            }

            return Collections.unmodifiableCollection(accessTokens);
        } else {
            return Collections.emptySet();
        }
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        byte[] key = this.serializeKey("client_id_to_access:" + clientId);
        return getOAuth2AccessTokenList(key);
    }
}
