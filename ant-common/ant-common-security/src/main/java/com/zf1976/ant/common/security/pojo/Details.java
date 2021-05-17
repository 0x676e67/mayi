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
    private final Set<String> permission;
    /**
     * 数据权限
     */
    private final Set<Long> dataPermission;
    /**
     * 用户信息
     */
    private final User userInfo;

    public Details(Set<String> permission, Set<Long> dataPermission, User userInfo) {
        this.permission = permission;
        this.dataPermission = dataPermission;
        this.userInfo = userInfo;
    }

    public Set<String> getPermission() {
        return permission;
    }


    public User getUserInfo() {
        return userInfo;
    }


    public Set<Long> getDataPermission() {
        return dataPermission;
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
