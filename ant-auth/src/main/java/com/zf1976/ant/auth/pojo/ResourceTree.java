package com.zf1976.ant.auth.pojo;

import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/5/6
 */
public class ResourceTree {

    /**
     * 资源树列表
     */
    private final List<Node> treeList;

    public ResourceTree(List<SysResource> resourceList) {
        treeList = new ArrayList<>();
        final Collection<Node> nodeCollection = resourceList.stream()
                                                            .map(Node::new)
                                                            .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
        for (Node var1 : nodeCollection) {
            // 根节点
            if (var1.pid == null) {
                this.treeList.add(var1);
            }
            for (Node var2 : nodeCollection) {
                if (var1.id.equals(var2.pid)) {
                    if (var1.children == null) {
                        var1.children = new ArrayList<>();
                    }
                    // 添加子节点
                    var1.children.add(var2);
                }
            }
        }
    }

    public List<Node> getTreeList() {
        return treeList;
    }

    public static class Node {

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
        private List<Node> children;

        public Node(SysResource sysResource) {
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

        public Node setId(Long id) {
            this.id = id;
            return this;
        }

        public Long getPid() {
            return pid;
        }

        public Node setPid(Long pid) {
            this.pid = pid;
            return this;
        }

        public String getName() {
            return name;
        }

        public Node setName(String name) {
            this.name = name;
            return this;
        }

        public String getUri() {
            return uri;
        }

        public Node setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public String getMethod() {
            return method;
        }

        public Node setMethod(String method) {
            this.method = method;
            return this;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public Node setEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Node setDescription(String description) {
            this.description = description;
            return this;
        }

        public Boolean getAllow() {
            return allow;
        }

        public Node setAllow(Boolean allow) {
            this.allow = allow;
            return this;
        }

        public List<Node> getChildren() {
            return children;
        }

        public Node setChildren(List<Node> children) {
            this.children = children;
            return this;
        }

        @Override
        public String toString() {
            return "Node{" +
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
}
