package com.zf1976.ant.auth.filter.provider;

import com.zf1976.ant.common.encrypt.EncryptUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * 默认 {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider} 不支持密码加密
 * 同时兼容 此为增强 {@link DaoAuthenticationEnhancerProvider}
 * @author mac
 * @date 2021/2/19
 **/
public class DaoAuthenticationEnhancerProvider extends AbstractUserDetailsAuthenticationProvider {
    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;
    private volatile String userNotFoundEncodedPassword;

    private DaoAuthenticationEnhancerProvider(Builder builder) {
        this.passwordEncoder = builder.passwordEncoder;
        this.userDetailsService = builder.userDetailsService;
    }

    @Override
    protected void doAfterPropertiesSet() {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String encryptPassword = authentication.getCredentials().toString();
            String presentedPassword;
            try {
                presentedPassword = EncryptUtil.decryptForRsaByPrivateKey(encryptPassword);
            } catch (Exception ignored) {
                presentedPassword = encryptPassword;
            }
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Authentication failed: password does not match stored value");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
    }

    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        this.prepareTimingAttackProtection();

        try {
            UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            } else {
                return loadedUser;
            }
        } catch (UsernameNotFoundException var4) {
            this.mitigateAgainstTimingAttack(authentication);
            throw var4;
        } catch (InternalAuthenticationServiceException var5) {
            throw var5;
        } catch (Exception var6) {
            throw new InternalAuthenticationServiceException(var6.getMessage(), var6);
        }
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode("userNotFoundPassword");
        }

    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public DaoAuthenticationEnhancerProvider setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        return this;
    }

    public DaoAuthenticationEnhancerProvider setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    protected UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder{
        private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        private UserDetailsService userDetailsService;

        public Builder(){

        }

        public Builder setPasswordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public Builder setUserDetailsService(UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
            return this;
        }

        public DaoAuthenticationEnhancerProvider build() {
            return new DaoAuthenticationEnhancerProvider(this);
        }
    }

}
