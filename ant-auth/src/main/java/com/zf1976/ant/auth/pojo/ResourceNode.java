package com.zf1976.ant.auth.pojo;

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
    private List<ResourceTree.Node> children;

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

    public ResourceNode setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getPid() {
        return pid;
    }

    public ResourceNode setPid(Long pid) {
        this.pid = pid;
        return this;
    }

    public String getName() {
        return name;
    }

    public ResourceNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public ResourceNode setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public ResourceNode setMethod(String method) {
        this.method = method;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public ResourceNode setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ResourceNode setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getAllow() {
        return allow;
    }

    public ResourceNode setAllow(Boolean allow) {
        this.allow = allow;
        return this;
    }

    public List<ResourceTree.Node> getChildren() {
        return children;
    }

    public ResourceNode setChildren(List<ResourceTree.Node> children) {
        this.children = children;
        return this;
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
