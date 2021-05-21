package com.zf1976.ant.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.common.component.cache.annotation.CacheConfig;
import com.zf1976.ant.common.component.cache.annotation.CacheEvict;
import com.zf1976.ant.common.component.cache.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
import com.zf1976.ant.upms.biz.convert.SysRoleConvert;
import com.zf1976.ant.upms.biz.dao.SysDepartmentDao;
import com.zf1976.ant.upms.biz.dao.SysMenuDao;
import com.zf1976.ant.upms.biz.dao.SysRoleDao;
import com.zf1976.ant.upms.biz.exception.RoleException;
import com.zf1976.ant.upms.biz.exception.enums.RoleState;
import com.zf1976.ant.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.ant.upms.biz.pojo.enums.DataPermissionEnum;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.po.SysMenu;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.upms.biz.pojo.query.RoleQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.role.RoleVO;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色表(SysRole)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:58
 */
@Service
@CacheConfig(namespace = Namespace.ROLE, dependsOn = Namespace.USER)
public class SysRoleService extends AbstractService<SysRoleDao, SysRole> {

    private final SysDepartmentDao sysDepartmentDao;
    private final SysMenuDao sysMenuDao;
    private final SysRoleConvert convert;

    public SysRoleService(SysDepartmentDao sysDepartmentDao, SysMenuDao sysMenuDao) {
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysMenuDao = sysMenuDao;
        this.convert = SysRoleConvert.INSTANCE;
    }

    /**
     * 所有角色 id，name
     *
     * @return /
     */
    @CachePut(key = "selectAllRole")
    public IPage<RoleVO> selectAll() {
        IPage<SysRole> page = super.lambdaQuery()
                                   .select(SysRole::getId, SysRole::getName)
                                   .page(new Page<>(1, 9999));
        return super.mapPageToTarget(page, this.convert::toVo);
    }

    /**
     * 分页查询角色
     *
     * @param query request page
     * @return /
     */
    @CachePut(key = "#query")
    @Transactional(readOnly = true)
    public IPage<RoleVO> selectRolePage(Query<RoleQueryParam> query) {
        IPage<SysRole> sourcePage = this.queryWrapper()
                                        .chainQuery(query)
                                        .selectPage();
        return super.mapPageToTarget(sourcePage, sysRole -> {
            sysRole.setDepartmentIds(this.selectRoleDepartmentIds(sysRole.getId()));
            sysRole.setMenuIds(this.selectRoleMenuIds(sysRole.getId()));
            return this.convert.toVo(sysRole);
        });
    }

    /**
     * 返回角色级别 0-999 数字越大级别越低
     *
     * @return math
     */
    @CachePut(dynamics = true)
    public Integer selectRoleLevel() {
        if (SessionManagement.isOwner()) {
            return -1;
        }
        return super.baseMapper.selectListByUsername(SessionManagement.getCurrentUsername())
                               .stream()
                               .map(SysRole::getLevel)
                               .min(Integer::compareTo)
                               .orElse(Integer.MAX_VALUE);
    }

    /**
     * 设置角色状态
     *
     * @param id 角色id
     * @param enabled 状态
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void updateRoleStatus(Long id, Boolean enabled) {
        if (!enabled) {
            // 存在用户关联不允许禁用当前角色
            if (super.baseMapper.selectUserDependsOnById(id) > 0) {
                throw new RoleException(RoleState.ROLE_DEPENDS_ERROR);
            }
        }
        boolean isUpdate = lambdaUpdate().set(SysRole::getEnabled, enabled)
                                         .eq(SysRole::getId, id)
                                         .update();
        if (!isUpdate) {
            throw new RoleException(RoleState.ROLE_NOT_FOUND);
        }
        return null;
    }

    /**
     * 查询角色
     *
     * @param id id
     * @return role
     */
    @Transactional(readOnly = true)
    public RoleVO selectRole(Long id) {
        final SysRole sysRole = super.lambdaQuery()
                                     .eq(SysRole::getId, id)
                                     .oneOpt().orElseThrow(() -> new RoleException(RoleState.ROLE_NOT_FOUND));
        sysRole.setDepartmentIds(this.selectRoleDepartmentIds(id));
        sysRole.setMenuIds(this.selectRoleMenuIds(id));
        return this.convert.toVo(sysRole);
    }

    /**
     * 获取角色所有部门
     *
     * @param id 角色id
     * @return department collection
     */
    private Set<Long> selectRoleDepartmentIds(Long id) {
        Assert.notNull(id, "role id cannot be null");
        return this.sysDepartmentDao.selectListByRoleId(id)
                                    .stream()
                                    .filter(SysDepartment::getEnabled)
                                    .map(SysDepartment::getId).collect(Collectors.toSet());
    }

    /**
     * 获取角色所有菜单id
     *
     * @param id 角色id
     * @return id collection
     */
    private Set<Long> selectRoleMenuIds(Long id) {
        Assert.notNull(id, "role id cannot be null");
        return this.sysMenuDao.selectListByRoleId(id)
                              .stream()
                              .map(SysMenu::getId)
                              .collect(Collectors.toSet());
    }

    /**
     * 新增角色
     *
     * @param dto role dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void savaRole(RoleDTO dto) {
        // 范围消息
        DataPermissionEnum permissionEnum = Optional.ofNullable(dto.getDataScope())
                                                    .orElseThrow(() -> new RoleException(RoleState.ROLE_OPT_ERROR));
        // 校验角色是否已存在
        super.lambdaQuery()
             .eq(SysRole::getName, dto.getName())
             .oneOpt()
             .ifPresent(sysRole -> {
                 throw new RoleException(RoleState.ROLE_EXISTING, sysRole.getName());
             });
        SysRole sysRole = this.convert.toEntity(dto);
        super.savaOrUpdate(sysRole);
        dto.setId(sysRole.getId());
        if (permissionEnum == DataPermissionEnum.ALL) {
            Set<Long> result = this.sysDepartmentDao.selectList(Wrappers.emptyWrapper())
                                                    .stream()
                                                    .map(SysDepartment::getId)
                                                    .collect(Collectors.toSet());
            dto.setDepartmentIds(result);
        }
        this.updateDependent(dto);
        return null;
    }

    /**
     * 更新角色
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void updateRole(RoleDTO dto) {
        // 范围消息
        DataPermissionEnum permissionEnum = Optional.ofNullable(dto.getDataScope())
                                                    .orElseThrow(() -> new RoleException(RoleState.ROLE_OPT_ERROR));
        //查询角色是否存在
        SysRole sysRole = super.lambdaQuery()
                               .eq(SysRole::getId, dto.getId())
                               .oneOpt()
                               .orElseThrow(() -> new RoleException(RoleState.ROLE_NOT_FOUND));
        switch (permissionEnum) {
            case ALL:
                Set<Long> dataPermission = this.sysDepartmentDao.selectList(null)
                                                                .stream()
                                                                .map(SysDepartment::getId)
                                                                .collect(Collectors.toSet());
                dto.setDepartmentIds(dataPermission);
                break;
            case LEVEL:
                dto.setDepartmentIds(Collections.emptySet());
                break;
            default:
                break;
        }
        this.convert.copyProperties(dto, sysRole);
        super.savaOrUpdate(sysRole);
        this.updateDependent(dto);
        return null;
    }

    /**
     * 更新 job menu 依赖关系
     * @param dto  dto
     */
    private void updateDependent(RoleDTO dto) {
        Set<Long> idList = Collections.singleton(dto.getId());
        Set<Long> departmentIds = dto.getDepartmentIds();
        if (!CollectionUtils.isEmpty(departmentIds)) {
            super.baseMapper.deleteDepartmentRelationByIds(idList);
            super.baseMapper.saveDepartmentRelationById(dto.getId(), departmentIds);
        }
        Set<Long> menuIds = dto.getMenuIds();
        if (!CollectionUtils.isEmpty(menuIds)) {
            super.baseMapper.deleteMenuRelationByIds(idList);
            super.baseMapper.saveMenuRelationById(dto.getId(), menuIds);
        }
    }

    /**
     * 删除角色
     *
     * @param ids id集合
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void deleteRole(Set<Long> ids) {
        ids.forEach(id -> {
            if (super.baseMapper.selectUserDependsOnById(id) > 0) {
                throw new RoleException(RoleState.ROLE_DEPENDS_ERROR);
            }
        });
        // 删除role
        super.deleteByIds(ids);
        // 删除role-menu
        super.baseMapper.deleteMenuRelationByIds(ids);
        // 删除role-department
        super.baseMapper.deleteDepartmentRelationByIds(ids);
        // 删除user-role
        super.baseMapper.deleteUserRelationByIds(ids);
        return null;
    }

}
