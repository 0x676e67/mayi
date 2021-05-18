package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.FluentIterable;
import com.zf1976.ant.auth.pojo.ResourceLink;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.auth.dao.SysResourceDao;
import com.zf1976.ant.auth.pojo.po.SysResource;
import com.zf1976.ant.common.component.cache.annotation.CacheConfig;
import com.zf1976.ant.common.component.cache.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/4/26
 */
@Service
@CacheConfig(namespace = Namespace.RESOURCE)
public class ResourceService extends ServiceImpl<SysResourceDao, SysResource> {


    public void test() {

        List<ResourceNode> resourceNodes = this.buildResourceTree(super.list());
        List<ResourceLink> resourceLinkList = this.buildResourceLinkList(resourceNodes);
    }

    @CachePut(key = "#page")
    @Transactional(readOnly = true)
    public IPage<ResourceNode> selectResourceNodeByPage(Page<SysResource> page) {
        // 独立根据根节点分页查询
        Page<SysResource> sourcePage = super.lambdaQuery()
                                      .isNull(SysResource::getPid)
                                      .page(page);

        // 查询所有根节点的子节点
        List<SysResource> childResourceList = super.lambdaQuery()
                                              .isNotNull(SysResource::getPid)
                                              .list();
        // 合并所有节点
        @SuppressWarnings("all")
        FluentIterable<SysResource> allResourceList = FluentIterable.concat(childResourceList, sourcePage.getRecords());
        // 根据分页出来的根节点构建资源树
        List<ResourceNode> resourceTreeList = this.buildResourceTree(allResourceList.toList());
        return new Page<ResourceNode>(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount()).setRecords(resourceTreeList);
    }

    /**
     * 构建资源树
     *
     * @date 2021-05-07 08:42:41
     * @param resourceList 资源列表
     * @return {@link List<ResourceNode>}
     */
    private List<ResourceNode> buildResourceTree(List<SysResource> resourceList) {
        List<ResourceNode> treeList = resourceList.stream()
                                                  .map(ResourceNode::new)
                                                  .collect(Collectors.toList());
        // 遍历所有根节点进行构造树
        for (ResourceNode var1 : treeList) {
            // 循环构造子节点
            for (ResourceNode var2 : treeList) {
                if (var1.getId().equals(var2.getPid())) {
                    if (var1.getChildren() == null) {
                        var1.setChildren(new ArrayList<>());
                    }
                    // 添加子节点
                    var1.getChildren().add(var2);
                }
            }
        }
        // 树节点进行递归处理，从根节点到各叶子uri进行链接，并给叶子设置完整路径
        return treeList.stream()
                       .filter(resourceNode -> {
                           // 根据树构建完整uri
                           if (resourceNode.getPid() == null) {
                               this.traverseNode(resourceNode);
                               return true;
                           }
                           return false;
                       })
                       .collect(Collectors.toList());
    }

    /**
     * 根据资源树构建资源链接列表
     *
     * @date 2021-05-07 23:43:49
     * @return {@link List<ResourceLink>}
     */
    private List<ResourceLink> buildResourceLinkList(List<ResourceNode> resourceNodeTree) {
        List<ResourceLink> resourceLinkList = new LinkedList<>();
        resourceNodeTree.forEach(resourceNode -> {
            this.traverseNode(resourceNode, resourceLinkList);
        });
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
        // 递归到叶子节点
        if (parentNode.getChildren() == null) {
            if (resourceLinkList != null) {
                // 构造完整资源链接
                ResourceLink resourceLink = new ResourceLink();
                resourceLink.setId(parentNode.getId())
                            .setName(parentNode.getName())
                            .setUri(parentNode.getUri())
                            .setMethod(parentNode.getMethod())
                            .setEnabled(parentNode.getEnabled())
                            .setAllow(parentNode.getAllow())
                            .setDescription(parentNode.getDescription());
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
     * 遍历节点构造完整URI
     *
     * @date 2021-05-07 23:40:54
     * @param parentNode 父节点
     */
    private void traverseNode(ResourceNode parentNode) {
        // 递归到叶子节点
        if (parentNode.getChildren() != null) {
            String parentUri = this.initResourceNodeFullUri(parentNode);
            for (ResourceNode childNode : parentNode.getChildren()) {
                String childUri = this.initResourceNodeFullUri(childNode);
                // 构造uri
                childNode.setFullUri(parentUri.concat(childUri));
                // 递归
                this.traverseNode(childNode);
            }
        }
    }

    private String initResourceNodeFullUri(ResourceNode resourceNode) {
        String parentUri = resourceNode.getFullUri();
        if (StringUtils.isEmpty(parentUri)) {
            parentUri = resourceNode.getUri();
        }
        return parentUri;
    }
}
