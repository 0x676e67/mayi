package com.zf1976.mayi.upms.biz.security.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.mayi.common.core.constants.SecurityConstants;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.dao.*;
import com.zf1976.mayi.upms.biz.pojo.po.*;
import com.zf1976.mayi.upms.biz.security.service.convert.UserConvert;
import com.zf1976.mayi.upms.biz.service.exception.UserException;
import com.zf1976.mayi.upms.biz.service.exception.enums.UserState;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/6/13
 */
@Service
public class AuthorizationUserService {

    private final SecurityProperties securityProperties;
    private final SysUserDao userDao;
    private final SysRoleDao roleDao;
    private final SysDepartmentDao departmentDao;
    private final SysPositionDao positionDao;
    private final SysMenuDao menuDao;
    private final UserConvert convert = UserConvert.INSTANCE;

    public AuthorizationUserService(SecurityProperties securityProperties, SysUserDao sysUserDao, SysRoleDao sysRoleDao, SysDepartmentDao sysDepartmentDao, SysPositionDao sysPositionDao, SysMenuDao menuDao) {
        this.securityProperties = securityProperties;
        this.userDao = sysUserDao;
        this.roleDao = sysRoleDao;
        this.departmentDao = sysDepartmentDao;
        this.positionDao = sysPositionDao;
        this.menuDao = menuDao;
    }

    public User findUserByUsername(String username) {
        // 初步验证用户是否存在
        SysUser sysUser = ChainWrappers.lambdaQueryChain(this.userDao)
                                       .eq(SysUser::getUsername, username)
                                       .oneOpt()
                                       .orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
        // 查询用户部门
        SysDepartment department = ChainWrappers.lambdaQueryChain(this.departmentDao)
                                                .eq(SysDepartment::getId, sysUser.getDepartmentId())
                                                .one();

        sysUser.setDepartment(department);
        // 查询用户角色
        List<SysRole> roleList = this.roleDao.selectBatchByUserId(sysUser.getId());
        sysUser.setRoleList(roleList);
        // 查询用户职位
        List<SysPosition> positionList = this.positionDao.selectBatchByUserId(sysUser.getId());
        sysUser.setPositionList(positionList);

        User user = this.convert.convert(sysUser);
        // 权限值
        Set<String> grantedAuthorities = this.grantedAuthorities(sysUser.getUsername(), sysUser.getId());
        // 数据权限
        Set<Long> grantedDataPermission = this.grantedDataPermission(sysUser.getUsername(), sysUser.getDepartmentId(), sysUser.getRoleList());
        user.setDataPermissions(grantedDataPermission);
        user.setPermissions(grantedAuthorities);
        return user;
    }

    /**
     * 获取用户数据权限
     * 实际上根据部门id作为数据范围值
     *
     * @param username  用户名
     * @param departmentId 部门id
     * @return 数据权限
     */
    private Set<Long> grantedDataPermission(String username, long departmentId, List<SysRole> roleList) {

        // 超级管理员
        if (ObjectUtils.nullSafeEquals(username, this.securityProperties.getOwner())) {
            return this.departmentDao.selectList(Wrappers.emptyWrapper())
                                     .stream()
                                     .map(SysDepartment::getId)
                                     .collect(Collectors.toSet());
        }

        // 用户级别角色排序
        final Set<SysRole> roles = roleList.stream()
                                        .sorted(Comparator.comparingInt(SysRole::getLevel))
                                        .collect(Collectors.toCollection(LinkedHashSet::new));
        // 数据权限范围
        final Set<Long> dataPermission = new HashSet<>();
        for (SysRole role : roles) {
            switch (Objects.requireNonNull(role.getDataScope())) {
                case LEVEL:
                    // 本级数据权限 用户部门
                    dataPermission.add(departmentId);
                    break;

                case CUSTOMIZE:
                    // 自定义用户/角色所在部门的数据权限
                    departmentDao.selectListByRoleId(role.getId())
                                 .stream()
                                 .map(SysDepartment::getId)
                                 .forEach(id -> {
                                     this.collectDepartmentTreeIds(id, dataPermission);
                                 });
                    dataPermission.add(departmentId);
                    break;

                default:
                    // 所有数据权限
                    return this.departmentDao.selectList(Wrappers.emptyWrapper())
                                             .stream()
                                             .map(SysDepartment::getId)
                                             .collect(Collectors.toSet());
            }
        }
        return dataPermission;
    }

    /**
     * 收集部门树id
     *
     * @param departmentId id
     * @param handler collect
     */
    private void collectDepartmentTreeIds(Long departmentId, Set<Long> handler) {
        Assert.notNull(departmentId, "department id can not been null");
        handler.add(departmentId);
        // 角色所有部门
        departmentDao.selectChildrenById(departmentId)
                     .stream()
                     .map(SysDepartment::getId)
                     .forEachOrdered(id -> {
                         this.collectDepartmentTreeIds(id, handler);
                     });
    }

    /**
     * 获取用户权限
     *
     * @param username 用户名
     * @param userId  用户id
     * @return 返回用户权限信息
     */
    private Set<String> grantedAuthorities(String username, long userId) {
        Set<String> authorities = new HashSet<>();
        String markerAdmin = securityProperties.getOwner();
        if (username.equals(markerAdmin)) {
            // 分配认证超级管理员角色
            authorities.add(SecurityConstants.ROLE + markerAdmin);
            return authorities;
        } else {
            List<SysRole> roles = roleDao.selectBatchByUserId(userId);
            authorities = roles.stream()
                               .flatMap(role -> menuDao.selectListByRoleId(role.getId()).stream())
                               .map(SysMenu::getPermission)
                               .filter(s -> !StringUtils.isEmpty(s))
                               .collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.emptySet();
        } else {
            return authorities;
        }
    }

}
