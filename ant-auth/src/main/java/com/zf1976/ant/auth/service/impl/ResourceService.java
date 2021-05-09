package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.auth.pojo.ResourceLink;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.upms.biz.dao.SysResourceDao;
import com.zf1976.ant.upms.biz.pojo.po.SysResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/4/26
 */
@Service
public class ResourceService extends ServiceImpl<SysResourceDao, SysResource> {

    /**
     * 获取资源链接
     *
     * @date 2021-05-07 23:43:49
     * @return {@link List< ResourceLink>}
     */
    public List<ResourceLink> getResourceLinkList() {
        final List<ResourceNode> nodeList = this.getResourceNodeList();
        List<ResourceLink> resourceLinkList = new LinkedList<>();
        for (ResourceNode node : nodeList) {
            this.traverseNode(node, resourceLinkList);
        }
        return resourceLinkList;
    }

    /**
     * 遍历节点构造完整URI
     *
     * @date 2021-05-07 23:40:54
     * @param parentNode 父节点
     * @param resourceLinkList 资源链接列表
     */
    private void traverseNode(ResourceNode parentNode, List<ResourceLink> resourceLinkList) {
        if (parentNode.getChildren() == null) {
            if (resourceLinkList != null) {
                // 构造完整资源链接
                ResourceLink resourceLink = ResourceLink.Builder.builder()
                                                         .id(parentNode.getId())
                                                         .name(parentNode.getName())
                                                         .uri(parentNode.getUri())
                                                         .method(parentNode.getMethod())
                                                         .enabled(parentNode.getEnabled())
                                                         .allow(parentNode.getAllow())
                                                         .description(parentNode.getDescription())
                                                         .build();
                resourceLinkList.add(resourceLink);
            }
        } else {
            String parentUri = parentNode.getUri();
            for (ResourceNode childNode : parentNode.getChildren()) {
                // 构造uri
                childNode.setUri(parentUri.concat(childNode.getUri()));
                // 递归
                this.traverseNode(childNode, resourceLinkList);
            }
        }
    }

    /**
     * 获取资源节点列表
     *
     * @date 2021-05-07 23:40:54
     * @return {@link List<ResourceNode>}
     */
    private List<ResourceNode> getResourceNodeList(){
        final List<SysResource> resourceList = super.lambdaQuery().list();
        return this.generatorResourceNode(resourceList);
    }

    /**
     * 构建资源树
     *
     * @date 2021-05-07 08:42:41
     * @param resourceList 资源列表
     * @return {@link List<ResourceNode>}
     */
    private List<ResourceNode> generatorResourceNode(List<SysResource> resourceList) {
        final List<ResourceNode> nodes = new ArrayList<>();
        final Collection<ResourceNode> nodeCollection = resourceList.stream()
                                                                    .map(ResourceNode::new)
                                                                    .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
        for (ResourceNode var1 : nodeCollection) {
            // 根节点
            if (var1.getPid() == null) {
                nodes.add(var1);
            }
            for (ResourceNode var2 : nodeCollection) {
                if (var1.getId().equals(var2.getPid())) {
                    if (var1.getChildren() == null) {
                        var1.setChildren(new ArrayList<>());
                    }
                    // 添加子节点
                    var1.getChildren().add(var2);
                }
            }
        }
        return nodes;
    }
}
