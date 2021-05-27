package com.zf1976.ant.auth.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.zf1976.ant.auth.dao.SysPermissionDao;
import com.zf1976.ant.auth.dao.SysResourceDao;
import com.zf1976.ant.auth.pojo.ResourceLink;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.auth.pojo.po.SysResource;
import com.zf1976.ant.common.component.cache.annotation.CacheConfig;
import com.zf1976.ant.common.component.cache.annotation.CachePut;
import com.zf1976.ant.common.core.constants.KeyConstants;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 动态资源服务
 *
 * @author mac
 * @date 2020/12/26
 **/
@Service
@CacheConfig(
        namespace = Namespace.RESOURCE,
        postInvoke = {"initialize"}
)
public class DynamicDataSourceService extends ServiceImpl<SysResourceDao, SysResource> implements InitPermission{

    private final Map<String, String> resourceMethodMap = new HashMap<>(16);
    private final Set<String> allowMethodSet = new HashSet<>(16);
    private final SysPermissionDao permissionDao;
    private final SecurityProperties securityProperties;

    public DynamicDataSourceService(SysPermissionDao sysPermissionDao, SecurityProperties securityProperties) {
        this.permissionDao = sysPermissionDao;
        this.securityProperties = securityProperties;
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
     * 查询资源链接信息
     *
     * @return {@link List<ResourceLink>}
     */
    @CachePut(key = "selectResourceLinkList")
    public List<ResourceLink> selectResourceLinkList() {
        List<SysResource> resourceList = super.lambdaQuery().list();
        List<ResourceNode> resourceTree = this.buildResourceTree(resourceList);
        return this.buildResourceLinkList(resourceTree);
    }

    /**
     * 构建资源树
     *
     * @param resourceList 资源列表
     * @return {@link List<ResourceNode>}
     * @date 2021-05-07 08:42:41
     */
    private List<ResourceNode> buildResourceTree(List<SysResource> resourceList) {
        List<ResourceNode> treeList = resourceList.stream()
                                                  .map(ResourceNode::new)
                                                  .collect(Collectors.toList());
        // 遍历所有根节点进行构造树
        for (ResourceNode var1 : treeList) {
            // 循环构造子节点
            for (ResourceNode var2 : treeList) {
                if (var1.getId()
                        .equals(var2.getPid())) {
                    if (var1.getChildren() == null) {
                        var1.setChildren(new ArrayList<>());
                    }
                    // 添加子节点
                    var1.getChildren()
                        .add(var2);
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
     * @return {@link List<ResourceLink>}
     * @date 2021-05-07 23:43:49
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
     * @param parentNode       父节点
     * @param resourceLinkList 资源链接列表
     * @date 2021-05-07 23:40:54
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
                            .setAllow(parentNode.getAllow());
                // 查询权限
                List<String> permissionList = this.permissionDao.selectPermissionsByResourceId(parentNode.getId());
                if (!CollectionUtils.isEmpty(permissionList)) {
                    String permissions = String.join(",", permissionList);
                    resourceLink.setPermissions(permissions);
                }
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
     * @param parentNode 父节点
     * @date 2021-05-07 23:40:54
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

    /**
     * 初始化节点fullUri属性
     *
     * @param resourceNode 资源节点
     * @return {@link String}
     */
    private String initResourceNodeFullUri(ResourceNode resourceNode) {
        String parentUri = resourceNode.getFullUri();
        if (StringUtils.isEmpty(parentUri)) {
            parentUri = resourceNode.getUri();
        }
        return parentUri;
    }


    /**
     * 加载动态数据源资源 （URI--Permissions）
     *
     * @return {@link Map}
     * @date 2021-05-05 19:53:43
     */
    @CachePut(key = KeyConstants.RESOURCES)
    @Transactional(readOnly = true)
    public Map<String, Collection<String>> loadDynamicDataSource() {
        //清空缓存
        if (!CollectionUtils.isEmpty(this.resourceMethodMap) || !CollectionUtils.isEmpty(allowMethodSet)) {
            this.resourceMethodMap.clear();
            this.allowMethodSet.clear();
        }
        Map<String, Collection<String>> resourcePermissionMap = new HashMap<>(16);
        // 所有资源
        List<SysResource> resourceList = super.lambdaQuery()
                                              .list();
        // 根节点资源
        List<SysResource> root = resourceList.stream()
                                             .filter(sysResource -> sysResource.getPid() == null)
                                             .collect(Collectors.toList());
        // 构造完整url 资源路径
        for (SysResource resource : root) {
            // 资源id-link
            Map<Long, String> resourceLink = new HashMap<>(16);
            // 资源id-method
            Map<Long, String> resourceMethod = new HashMap<>(16);
            this.buildResourceLink(resource, resourceLink, resourceMethod, resourceList);
            resourceLink.forEach((id, path) -> {
                // 权限值
                List<String> permissions = this.permissionDao.selectPermissionsByResourceId(id)
                                                             .stream()
                                                             .distinct()
                                                             .collect(Collectors.toList());
                // 方法
                String method = resourceMethod.get(id);
                resourcePermissionMap.put(path, permissions);
                this.resourceMethodMap.put(path, method);
            });

        }
        return resourcePermissionMap;
    }

    /**
     * 构造资源路径
     *
     * @param parent          节点资源
     * @param resourceLinkMap id-path映射
     * @param methodMap       id-method映射
     * @date 2021-05-05 19:56:08
     */
    private void buildResourceLink(SysResource parent, Map<Long, String> resourceLinkMap, Map<Long, String> methodMap, List<SysResource> resourceList) {
        var parentId = parent.getId();
        var parentUri = parent.getUri();
        var parentMethod = parent.getMethod();
        // 判断是否make
        var condition = true;
        List<SysResource> childResourceList = resourceList.stream()
                                                          .filter(sysResource -> ObjectUtils.nullSafeEquals(parentId, sysResource.getPid()))
                                                          .collect(Collectors.toList());
        for (SysResource child : childResourceList) {
            // 路径深搜
            child.setUri(parentUri.concat(child.getUri()));
            // 继续make
            this.buildResourceLink(child, resourceLinkMap, methodMap, resourceList);
            // make完成
            condition = false;
        }
        if (condition) {
            resourceLinkMap.put(parentId, parentUri);
            methodMap.put(parentId, parentMethod);
            // 是否allow
            if (parent.getAllow()) {
                this.allowMethodSet.add(parentUri);
            }
        }
    }

    @CachePut(key = KeyConstants.MATCH_METHOD)
    public Map<String, String> getResourceMethodMap() {
        if (CollectionUtils.isEmpty(this.resourceMethodMap)) {
            this.loadDynamicDataSource();
        }
        return this.resourceMethodMap;
    }

    /**
     * redis 反序化回来变成set
     *
     * @return getAllowUri
     */
    @CachePut(key = KeyConstants.ALLOW)
    public List<String> loadAllowUri() {
        CollectionUtils.mergeArrayIntoCollection(securityProperties.getIgnoreUri(), this.allowMethodSet);
        return Lists.newArrayList(this.allowMethodSet);
    }


    @Override
    public void initialize() {
        this.loadDynamicDataSource();
        this.loadAllowUri();
    }
}
