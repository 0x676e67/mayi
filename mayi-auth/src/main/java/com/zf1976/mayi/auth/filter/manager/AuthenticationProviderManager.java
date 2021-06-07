package com.zf1976.mayi.auth.filter.manager;

import com.zf1976.mayi.auth.filter.provider.CaptchaAuthenticationProvider;
import com.zf1976.mayi.auth.filter.provider.SecurityAuthenticationProvider;
import com.zf1976.mayi.auth.filter.provider.WebSessionRegisterProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义认证管理器
 *
 * @author ant
 * Create by Ant on 2020/9/12 9:58 上午
 */
@Deprecated
public class AuthenticationProviderManager implements AuthenticationManager {

    private final List<AuthenticationProvider> providers;
    private final AuthenticationManager parent;

    public AuthenticationProviderManager() {
        this(new CaptchaAuthenticationProvider(), new SecurityAuthenticationProvider(), new WebSessionRegisterProvider());
    }



    public AuthenticationProviderManager(AuthenticationProvider... providers) {
        this(Arrays.asList(providers), null);
    }

    public AuthenticationProviderManager(List<AuthenticationProvider> providers, AuthenticationManager parent) {
        Assert.notNull(providers, "providers list cannot be null");
        this.providers = providers;
        this.parent = parent;
        checkState();
    }

    private void checkState() {
        if (this.parent == null && this.providers.isEmpty()) {
            throw new IllegalArgumentException("A parent AuthenticationManager or a list of AuthenticationProviders is required");
        } else if (this.providers.contains(null)) {
            throw new IllegalArgumentException("providers list cannot contain null values");
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final Class<? extends Authentication> tokTest = authentication.getClass();
        AuthenticationException firstException = null;
        Authentication result = null;
        for (AuthenticationProvider provider : this.getProviders()) {
            if (provider.supports(tokTest)) {
                try {
                    if (result != null) {
                        result = provider.authenticate(result);
                        continue;
                    }
                    result = provider.authenticate(authentication);
                    this.copyDetails(authentication, result);
                } catch (AuthenticationException e) {
                    firstException = e;
                    break;
                }
            }
        }
        if (firstException != null) {
            throw firstException;
        }
        return result;
    }

    private void copyDetails(Authentication source, Authentication target) {
        if (target instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) target;
            token.setDetails(source.getDetails());
        }

    }

    public List<AuthenticationProvider> getProviders() {
        return this.providers;
    }


}
