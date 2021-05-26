package com.zf1976.ant.auth.service;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.auth.dao.SysPermissionDao;
import com.zf1976.ant.auth.dao.SysResourceDao;
import com.zf1976.ant.auth.exception.SecurityException;
import com.zf1976.ant.auth.pojo.po.SysResource;
import com.zf1976.ant.common.component.cache.annotation.CacheEvict;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Service
public class PermissionBindingService {

    private final SysPermissionDao permissionDao;
    private final SysResourceDao resourceDao;
    private final SysRoleDao roleDao;

    public PermissionBindingService(SysPermissionDao permissionDao,
                                    SysResourceDao resourceDao,
                                    SysRoleDao roleDao) {
        this.permissionDao = permissionDao;
        this.resourceDao = resourceDao;
        this.roleDao = roleDao;
    }

    /**
     * 绑定角色权限
     *
     * @param id 角色id
     * @param permissionIdList 权限id列表
     * @return {@link Void}
     */
    public Void bindingRole(Long id, Set<Long> permissionIdList) {
        if (CollectionUtils.isEmpty(permissionIdList)) {
            throw new SecurityException("权限值不能为空");
        }
        // 查找当前绑定角色是否存在
        ChainWrappers.lambdaQueryChain(this.roleDao)
                     .eq(SysRole::getId, id)
                     .oneOpt()
                     .orElseThrow(() -> new SecurityException("绑定角色不存在"));
        // 保存角色-资源关系
        this.permissionDao.saveRoleRelation(id, permissionIdList);
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
            throw new SecurityException("权限值不能为空");
        }
        // 查找当前绑定资源是否存在
        final SysResource resource = ChainWrappers.lambdaQueryChain(this.resourceDao)
                                          .eq(SysResource::getId, id)
                                          .oneOpt()
                                          .orElseThrow(() -> new SecurityException("绑定资源不存在"));
        // 当前资源节点不属于叶子节点，不允许绑定
        if (resource.getPid() == null || !resource.getLeaf()) {
            throw new SecurityException("当前资源节点不允许绑定");
        }
        // 保存权限-资源关系
        this.permissionDao.saveResourceRelation(resource.getId(), permissionIdList);
        return null;
    }
}
