package com.zf1976.mayi.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zf1976.mayi.upms.biz.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:00
 */
public class LoginUserDetails implements UserDetails {

    private final User delegate;

    public LoginUserDetails(User user) {
        this.delegate = user;
    }

    public Set<String> getPermission() {
        return this.delegate.getPermissions();
    }

    public Set<Long> getDataPermission() {
        return this.delegate.getDataPermissions();
    }

    public User getDelegate() {
        return this.delegate;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", this.getPermission()));
    }

    @JsonIgnore
    public Long getId() {
        return this.delegate.getId();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return delegate.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return delegate.getUsername();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return this.isEnabled();
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return delegate.getEnabled();
    }
}
