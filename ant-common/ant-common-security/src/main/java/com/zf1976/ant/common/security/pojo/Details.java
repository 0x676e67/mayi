package com.zf1976.ant.common.security.pojo;


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
    private Set<Long> dataPermission;
    /**
     * 用户信息
     */
    private User user;

    public Details() {
    }

    public Details(Set<String> permission, Set<Long> dataPermission, User user) {
        this.permission = permission;
        this.dataPermission = dataPermission;
        this.user = user;
    }

    public Set<String> getPermission() {
        return permission;
    }

    public void setPermission(Set<String> permission) {
        this.permission = permission;
    }

    public Set<Long> getDataPermission() {
        return dataPermission;
    }

    public void setDataPermission(Set<Long> dataPermission) {
        this.dataPermission = dataPermission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "permission=" + permission +
                ", dataPermission=" + dataPermission +
                ", userInfo=" + user +
                '}';
    }
}
