package com.zf1976.ant.auth.service;

/**
 * 权限角色、权限资源在清除缓存后重新初始化
 * @author mac
 * @date 2021/5/27
 */
public interface InitPermission {

    /**
     * 角色资源权限初始化
     */
    void initialize();
}
