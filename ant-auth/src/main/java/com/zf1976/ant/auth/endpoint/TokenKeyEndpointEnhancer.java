package com.zf1976.ant.auth.endpoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;

/**
 * @author ant
 * Create by Ant on 2021/3/13 8:53 AM
 */
@RestController
public class TokenKeyEndpointEnhancer {

    private final KeyPair keyPair;

    public TokenKeyEndpointEnhancer(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping("/oauth/token_key")
    public Serializable getKey(Principal principal) {
        if (principal == null && (this.keyPair.getPublic() == null)) {
            throw new AccessDeniedException("You need to authenticate to see a shared key");
        } else {
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAKey key = new RSAKey.Builder(publicKey).build();
            return new JWKSet(key).toJSONObject();
        }
    }

}
