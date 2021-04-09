package com.zf1976.ant.common.security.pojo;


import java.io.Serializable;
import java.util.Set;

/**
 * @author mac
 * @date 2021/4/6
 */

public class AuthUserDetails implements Serializable {

    /**
     * 权限
     */
    private Set<String> permission;
    /**
     * 数据权限
     */
    private Set<Long> dataPermission;
    /**
     * 用户信息
     */
    private UserInfo userInfo;

    public Set<String> getPermission() {
        return permission;
    }

    public AuthUserDetails setPermission(Set<String> permission) {
        this.permission = permission;
        return this;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public AuthUserDetails setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public Set<Long> getDataPermission() {
        return dataPermission;
    }

    public AuthUserDetails setDataPermission(Set<Long> dataPermission) {
        this.dataPermission = dataPermission;
        return this;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "permission=" + permission +
                ", dataPermission=" + dataPermission +
                ", userInfo=" + userInfo +
                '}';
    }


    public static final class UserDetailsBuilder {
        private Set<String> permission;
        private Set<Long> dataPermission;
        private UserInfo userInfo;

        private UserDetailsBuilder() {
        }

        public static UserDetailsBuilder builder() {
            return new UserDetailsBuilder();
        }

        public UserDetailsBuilder permission(Set<String> permission) {
            this.permission = permission;
            return this;
        }

        public UserDetailsBuilder dataPermission(Set<Long> dataPermission) {
            this.dataPermission = dataPermission;
            return this;
        }

        public UserDetailsBuilder userInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public AuthUserDetails build() {
            AuthUserDetails userDetails = new AuthUserDetails();
            userDetails.setPermission(permission);
            userDetails.setDataPermission(dataPermission);
            userDetails.setUserInfo(userInfo);
            return userDetails;
        }
    }
}
