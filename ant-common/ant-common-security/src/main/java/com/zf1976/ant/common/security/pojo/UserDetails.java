package com.zf1976.ant.common.security.pojo;


import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author mac
 * @date 2021/4/6
 */

public class UserDetails implements Serializable {

    /**
     * 权限
     */
    private List<String> permission;
    /**
     * 用户信息
     */
    private UserInfo userInfo;

    public List<String> getPermission() {
        return permission;
    }

    public UserDetails setPermission(List<String> permission) {
        this.permission = permission;
        return this;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public UserDetails setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "permission=" + permission +
                ", userInfo=" + userInfo +
                '}';
    }

    public static final class UserDetailsBuilder {
        private List<String> permission;
        private UserInfo userInfo;

        private UserDetailsBuilder() {
        }

        public static UserDetailsBuilder anUserDetails() {
            return new UserDetailsBuilder();
        }

        public UserDetailsBuilder withPermission(List<String> permission) {
            this.permission = permission;
            return this;
        }

        public UserDetailsBuilder withUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public UserDetails build() {
            UserDetails userDetails = new UserDetails();
            userDetails.permission = this.permission;
            userDetails.userInfo = this.userInfo;
            return userDetails;
        }
    }
}
