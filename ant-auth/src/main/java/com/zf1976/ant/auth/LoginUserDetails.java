package com.zf1976.ant.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zf1976.ant.common.security.pojo.UserInfo;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:00
 */
public class LoginUserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private final UserInfo userInfo;
    private final Set<Long> dataScopes;
    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUserDetails(UserInfo userInfo, Collection<? extends GrantedAuthority> authorities, Set<Long> dataScopes) {
        this.userInfo = userInfo;
        this.dataScopes = dataScopes;
        this.authorities = authorities;
    }

    public Set<String> getPermission() {
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .collect(Collectors.toSet());
    }

    public Set<Long> getDataScopes() {
        return this.dataScopes;
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    public Long getId() {
        return this.userInfo.getId();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfo.getUsername();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
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
        return userInfo.getEnabled();
    }
}
