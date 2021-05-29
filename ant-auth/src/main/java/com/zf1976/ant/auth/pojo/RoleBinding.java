package com.zf1976.ant.auth.pojo;

import java.util.List;

/**
 * @author mac
 * @date 2021/5/29
 */
public class RoleBinding {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 数据权限
     */
    private Integer dataScope;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 权限列表
     */
    private List<BindingPermission> bindingPermissions;

    public Long getId() {
        return id;
    }

    public RoleBinding setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public RoleBinding setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoleBinding setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RoleBinding setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public RoleBinding setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public List<BindingPermission> getBindingPermissions() {
        return bindingPermissions;
    }

    public RoleBinding setBindingPermissions(List<BindingPermission> bindingPermissions) {
        this.bindingPermissions = bindingPermissions;
        return this;
    }

    @Override
    public String toString() {
        return "RoleBinding{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataScope=" + dataScope +
                ", enabled=" + enabled +
                ", bindingPermissions=" + bindingPermissions +
                '}';
    }
}
