package com.zf1976.ant.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zf1976.ant.common.security.pojo.User;
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

    private final User user;
    private final Set<Long> dataPermission;
    private final Set<String> permission;

    public LoginUserDetails(User user, Collection<? extends GrantedAuthority> authorities, Set<Long> dataPermission) {
        this.user = user;
        this.dataPermission = dataPermission;
        this.permission = AuthorityUtils.authorityListToSet(authorities);
    }

    public Set<String> getPermission() {
        return this.permission;
    }

    public Set<Long> getDataPermission() {
        return this.dataPermission;
    }

    public User getUser() {
        return this.user;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final String[] authority = permission.toArray(new String[1]);
        return AuthorityUtils.createAuthorityList(authority);
    }

    @JsonIgnore
    public Long getId() {
        return this.user.getId();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getUsername();
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
        return user.getEnabled();
    }
}
