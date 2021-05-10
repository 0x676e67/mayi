package com.zf1976.ant.auth.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;

import java.util.List;

/**
 * @author mac
 * @date 2021/5/6
 */
public class ResourceNode {

    private Long id;
    /**
     * pid resource
     */
    private Long pid;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源url
     */
    private String uri;
    /**
     * 完整uri, 从父节点到当前节点到uri拼接
     */
    private String fullUri;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 开关
     */
    private Boolean enabled;
    /**
     * 资源描述
     */
    private String description;
    /**
     * 放行
     */
    private Boolean allow;
    /**
     * 子节点
     */
    private List<ResourceNode> children;

    public ResourceNode(SysResource sysResource) {
        this.id = sysResource.getId();
        this.pid = sysResource.getPid();
        this.name = sysResource.getName();
        this.uri = sysResource.getUri();
        this.method = sysResource.getMethod();
        this.enabled = sysResource.getEnabled();
        this.description = sysResource.getDescription();
        this.allow = sysResource.getAllow();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFullUri() {
        return fullUri;
    }

    public void setFullUri(String fullUri) {
        this.fullUri = fullUri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAllow() {
        return allow;
    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    public List<ResourceNode> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ResourceNode{" +
                "id=" + id +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", method='" + method + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                ", allow=" + allow +
                ", children=" + children +
                '}';
    }
}
