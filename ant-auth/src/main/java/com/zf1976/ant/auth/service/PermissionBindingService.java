package com.zf1976.ant.auth.service;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.auth.dao.SysPermissionDao;
import com.zf1976.ant.auth.dao.SysResourceDao;
import com.zf1976.ant.auth.exception.SecurityException;
import com.zf1976.ant.auth.pojo.ResourceLinkBinding;
import com.zf1976.ant.auth.pojo.ResourceNode;
import com.zf1976.ant.auth.pojo.RoleBinding;
import com.zf1976.ant.auth.pojo.po.SysResource;
import com.zf1976.ant.common.component.cache.annotation.CacheConfig;
import com.zf1976.ant.common.component.cache.annotation.CacheEvict;
import com.zf1976.ant.common.component.cache.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
@CacheConfig(
        namespace = Namespace.PERMISSION,
        dependsOn = {Namespace.ROLE, Namespace.RESOURCE},
        postInvoke = {"initialize"}
)
public class PermissionBindingService implements InitPermission{

    private final SysPermissionDao permissionDao;
    private final SysResourceDao resourceDao;
    private final SysRoleDao roleDao;
    private PermissionService permissionService;
    private DynamicDataSourceService dynamicDataSourceService;

    public PermissionBindingService(SysPermissionDao permissionDao, SysResourceDao resourceDao, SysRoleDao roleDao) {
        this.permissionDao = permissionDao;
        this.resourceDao = resourceDao;
        this.roleDao = roleDao;
    }

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setDynamicDataSourceService(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

    /**
     * 查询绑定权限角色列表
     *
     * @return {@link List<RoleBinding>}
     */
    @CachePut(key = "selectRoleBindingList")
    public List<RoleBinding> selectRoleBindingList() {
        return permissionDao.selectRoleBindingList();
    }

    /**
     * 查询绑定权限资源链接列表
     *
     * @return {@link List<ResourceLinkBinding>}
     */
    @CachePut(key = "selectResourceLinkBindingList")
    public List<ResourceLinkBinding> selectResourceLinkBindingList() {
        // 资源列表
        List<SysResource> resourceList = this.dynamicDataSourceService.list();
        // 资源树
        List<ResourceNode> resourceNodeList = this.dynamicDataSourceService.buildResourceTree(resourceList);
        // 构建资源绑定权限链接
        return this.dynamicDataSourceService.buildResourceLinkBindingList(resourceNodeList);
    }


    /**
     * 绑定角色权限
     *
     * @param id 角色id
     * @param permissionIdList 权限id列表
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void bindingRole(Long id, Set<Long> permissionIdList) {
        if (CollectionUtils.isEmpty(permissionIdList)) {
            throw new SecurityException("The permission value cannot be empty");
        }
        // 查找当前绑定角色是否存在
        ChainWrappers.lambdaQueryChain(this.roleDao)
                     .eq(SysRole::getId, id)
                     .oneOpt()
                     .orElseThrow(() -> new SecurityException("Bound role does not exist"));
        // 保存角色-资源关系
        try {
            this.permissionDao.saveRoleRelation(id, permissionIdList);
        } catch (Exception e) {
            // 异常转译
            throw new SecurityException("Non-repeatable binding", e.getCause());
        }
        return null;
    }

    /**
     * 权限绑定资源
     *
     * @param id 资源id
     * @param permissionIdList 权限id列表
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void bindingResource(Long id, Set<Long> permissionIdList) {
        if (CollectionUtils.isEmpty(permissionIdList)) {
            throw new SecurityException("The permission value cannot be empty");
        }
        // 查找当前绑定资源是否存在
        final SysResource resource = ChainWrappers.lambdaQueryChain(this.resourceDao)
                                          .eq(SysResource::getId, id)
                                          .oneOpt()
                                          .orElseThrow(() -> new SecurityException("The bound resource does not exist"));
        // 当前资源节点不属于叶子节点，不允许绑定
        if (resource.getPid() == null || !resource.getLeaf()) {
            throw new SecurityException("The current resource node does not allow binding");
        }
        // 保存权限-资源关系
        try {
            this.permissionDao.saveResourceRelation(resource.getId(), permissionIdList);
        } catch (Exception e) {
            // 异常转译
            throw new SecurityException("Non-repeatable binding", e.getCause());
        }
        return null;
    }

    /**
     * 解绑资源权限
     *
     * @param id  资源id
     * @param permissionIdList 权限id集合
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void unbindingResource(Long id, Set<Long> permissionIdList) {
        if (CollectionUtils.isEmpty(permissionIdList)) {
            throw new SecurityException("The permission value cannot be empty");
        }
        // 查找当前绑定资源是否存在
        ChainWrappers.lambdaQueryChain(this.resourceDao)
                     .eq(SysResource::getId, id)
                     .oneOpt()
                     .orElseThrow(() -> new SecurityException("The bound resource does not exist"));
        // 进行资源权限解绑
        try {
            this.permissionDao.deleteResourceRelationByResourceIdAndPermissionIdList(id, permissionIdList);
        } catch (Exception e) {
            // 异常转译
            throw new SecurityException("Failed to unbind resource permissions", e.getCause());
        }
        return null;
    }

    /**
     * 解绑角色权限
     *
     * @param id  角色id
     * @param permissionIdList 权限id集合
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void unbindingRole(Long id, Set<Long> permissionIdList) {
        if (CollectionUtils.isEmpty(permissionIdList)) {
            throw new SecurityException("The permission value cannot be empty");
        }
        // 查找当前绑定角色是否存在
        ChainWrappers.lambdaQueryChain(this.roleDao)
                     .eq(SysRole::getId, id)
                     .oneOpt()
                     .orElseThrow(() -> new SecurityException("Bound role does not exist"));
        // 保存角色-资源关系
        try {
            this.permissionDao.deleteRoleRelationByRoleIdAndPermissionIdList(id, permissionIdList);
        } catch (Exception e) {
            // 异常转译
            throw new SecurityException("Failed to unbind role permissions", e.getCause());
        }
        return null;
    }

    @Override
    public void initialize() {
        this.permissionService.initialize();
    }
}
