package com.zf1976.ant.auth.pojo;

/**
 * @author mac
 * @date 2021/5/6
 */
public class ResourceLink {

    private Long id;
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

    public Long getId() {
        return id;
    }

    public ResourceLink setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ResourceLink setName(String name) {
        this.name = name;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public ResourceLink setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public ResourceLink setMethod(String method) {
        this.method = method;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public ResourceLink setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ResourceLink setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getAllow() {
        return allow;
    }

    public ResourceLink setAllow(Boolean allow) {
        this.allow = allow;
        return this;
    }

    @Override
    public String toString() {
        return "ResourceLink{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", method='" + method + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                ", allow=" + allow +
                '}';
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String uri;
        private String method;
        private Boolean enabled;
        private String description;
        private Boolean allow;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder allow(Boolean allow) {
            this.allow = allow;
            return this;
        }

        public ResourceLink build() {
            ResourceLink resourceLink = new ResourceLink();
            resourceLink.uri = this.uri;
            resourceLink.name = this.name;
            resourceLink.enabled = this.enabled;
            resourceLink.id = this.id;
            resourceLink.method = this.method;
            resourceLink.allow = this.allow;
            resourceLink.description = this.description;
            return resourceLink;
        }
    }
}
