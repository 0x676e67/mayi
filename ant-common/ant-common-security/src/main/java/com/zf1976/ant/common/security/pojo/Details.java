package com.zf1976.ant.common.security.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Set;

/**
 * @author mac
 * @date 2021/4/6
 */

public class Details implements Serializable {

    /**
     * 权限
     */
    private Set<String> permission;
    /**
     * 数据权限
     */
    @JsonIgnore
    private Set<Long> dataPermission;
    /**
     * 用户信息
     */
    private User userInfo;

    public Details() {
    }

    public Details(Set<String> permission, Set<Long> dataPermission, User userInfo) {
        this.permission = permission;
        this.dataPermission = dataPermission;
        this.userInfo = userInfo;
    }

    public Set<String> getPermission() {
        return permission;
    }

    public void setPermission(Set<String> permission) {
        this.permission = permission;
    }

    @JsonIgnore
    public Set<Long> getDataPermission() {
        return dataPermission;
    }

    public void setDataPermission(Set<Long> dataPermission) {
        this.dataPermission = dataPermission;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "permission=" + permission +
                ", dataPermission=" + dataPermission +
                ", userInfo=" + userInfo +
                '}';
    }
}
