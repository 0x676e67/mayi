package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.auth.LoginUserDetails;
import com.zf1976.ant.auth.convert.UserConvert;
import com.zf1976.ant.auth.exception.UserNotFountException;
import com.zf1976.ant.auth.service.UserDetailsServiceEnhancer;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.security.enums.AuthenticationState;
import com.zf1976.ant.common.security.pojo.Details;
import com.zf1976.ant.common.security.pojo.User;
import com.zf1976.ant.common.security.pojo.Role;
import com.zf1976.ant.common.security.property.SecurityProperties;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import com.zf1976.ant.upms.biz.dao.*;
import com.zf1976.ant.upms.biz.pojo.po.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:02
 */
@Service("userDetailsService")
public class SecurityUserDetailsServiceImpl implements UserDetailsServiceEnhancer {

    private final SecurityProperties securityProperties;
    private final SysDepartmentDao sysDepartmentDao;
    private final SysUserDao sysUserDao;
    private final SysRoleDao sysRoleDao;
    private final SysMenuDao sysMenuDao;
    private final SysPositionDao positionDao;
    private final UserConvert convert;

    public SecurityUserDetailsServiceImpl(SecurityProperties properties,
                                          SysDepartmentDao sysDepartmentDao,
                                          SysUserDao sysUserDao,
                                          SysRoleDao sysRoleDao,
                                          SysMenuDao sysMenuDao,
                                          SysPositionDao positionDao) {
        this.sysUserDao = sysUserDao;
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysRoleDao = sysRoleDao;
        this.sysMenuDao = sysMenuDao;
        this.securityProperties = properties;
        this.positionDao = positionDao;
        this.convert = UserConvert.INSTANCE;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        final User user = this.getUserInfo(username);
        if (!user.getEnabled()) {
            throw new UserNotFountException(AuthenticationState.ACCOUNT_DISABLED);
        }
        // 权限值
        List<GrantedAuthority> grantedAuthorities = this.grantedAuthorities(user);
        // 数据权限
        Set<Long> grantedDataPermission = this.grantedDataPermission(user);
        return new LoginUserDetails(user, grantedAuthorities, grantedDataPermission);
    }

    private User getUserInfo(String username) {
        // 初步验证用户是否存在
        SysUser sysUser = ChainWrappers.lambdaQueryChain(this.sysUserDao)
                                       .eq(SysUser::getUsername, username)
                                       .oneOpt()
                                       .orElseThrow(() -> new UserNotFountException(AuthenticationState.USER_NOT_FOUNT));
        // 查询用户部门
        SysDepartment department = ChainWrappers.lambdaQueryChain(this.sysDepartmentDao)
                                                .eq(SysDepartment::getId, sysUser.getDepartmentId())
                                                .one();
        // 查询用户角色
        List<SysRole> roleList = this.sysRoleDao.selectListByUserId(sysUser.getId());
        // 查询用户职位
        List<SysPosition> positionList = this.positionDao.selectListByUserId(sysUser.getId());
        sysUser.setDepartment(department);
        sysUser.setRoleList(roleList);
        sysUser.setPositionList(positionList);
        return this.convert.convert(sysUser);
    }

    /**
     * 存在用户则返回用户
     *
     * @param username 用户名
     * @return user
     */
    private SysUser findUsername(String username) {
        SysUser sysUser = sysUserDao.findByUsername(username);
        Optional.ofNullable(sysUserDao.findByUsername(username))
                .orElseThrow(() -> new UserNotFountException(AuthenticationState.USER_NOT_FOUNT));
        return sysUser;
    }

    /**
     * 获取用户数据权限
     * 实际上根据部门id作为数据范围值
     *
     * @param userInfo 用户信息
     * @return 数据权限
     */
    private Set<Long> grantedDataPermission(User userInfo) {

        // 超级管理员
        if (ObjectUtils.nullSafeEquals(userInfo.getUsername(), this.securityProperties.getOwner())) {
            return this.sysDepartmentDao.selectList(Wrappers.emptyWrapper())
                                        .stream()
                                        .map(SysDepartment::getId)
                                        .collect(Collectors.toSet());
        }

        // 用户级别角色排序
        final Set<Role> roles = userInfo.getRoleList()
                                        .stream()
                                        .sorted(Comparator.comparingInt(Role::getLevel))
                                        .collect(Collectors.toCollection(LinkedHashSet::new));
        // 数据权限范围
        final Set<Long> dataPermission = new HashSet<>();
        for (Role role : roles) {
            switch (Objects.requireNonNull(role.getDataScope())) {
                case LEVEL:
                    // 本级数据权限 用户部门
                    dataPermission.add(userInfo.getDepartment().getId());
                    break;

                case CUSTOMIZE:
                    // 自定义用户/角色所在部门的数据权限
                    sysDepartmentDao.selectListByRoleId(role.getId())
                                    .stream()
                                    .map(SysDepartment::getId)
                                    .forEach(id -> {
                                  this.collectDepartmentTreeIds(id, () -> dataPermission);
                              });
                    dataPermission.add(userInfo.getDepartment().getId());
                    break;

                default:
                    // 所有数据权限
                    return this.sysDepartmentDao.selectList(Wrappers.emptyWrapper())
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
    public void collectDepartmentTreeIds(Long departmentId, Supplier<Set<Long>> handler) {
        Assert.notNull(departmentId, "department id can not been null");
        handler.get().add(departmentId);
        // 角色所有部门
        sysDepartmentDao.selectChildrenById(departmentId)
                        .stream()
                        .map(SysDepartment::getId)
                        .forEachOrdered(id -> {
                      this.collectDepartmentTreeIds(id, handler);
                  });
    }

    /**
     * 获取用户权限
     *
     * @param userInfo info
     * @return 返回用户权限信息
     */
    private List<GrantedAuthority> grantedAuthorities(User userInfo) {
        Set<String> authorities = new HashSet<>();
        String markerAdmin = securityProperties.getOwner();
        if (userInfo.getUsername().equals(markerAdmin)) {
            // 分配认证超级管理员角色
            authorities.add("ROLE_" + markerAdmin);
            return authorities.stream()
                              .map(SimpleGrantedAuthority::new)
                              .collect(Collectors.toList());
        } else {
            List<SysRole> sysRoles = sysRoleDao.selectListByUserId(userInfo.getId());
            authorities = sysRoles.stream()
                                  .flatMap(sysRole -> sysMenuDao.selectListByRoleId(sysRole.getId()).stream())
                                  .map(SysMenu::getPermission)
                                  .filter(s -> !StringUtils.isEmpty(s))
                                  .collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.emptyList();
        } else {
            return authorities.stream()
                              .map(SimpleGrantedAuthority::new)
                              .collect(Collectors.toList());
        }
    }

    @Override
    public Details selectUserDetails(String username){
        LoginUserDetails userDetails = (LoginUserDetails) this.loadUserByUsername(username);
        return Details.UserDetailsBuilder.builder()
                                         .userInfo(userDetails.getUserInfo())
                                         .permission(userDetails.getPermission())
                                         .dataPermission(userDetails.getDataPermission())
                                         .build();
    }

    @Override
    @CachePut(namespace = Namespace.USER, dynamics = true)
    public Details selectUserDetails() {
        final String username = SessionManagement.getUsername();
        return this.selectUserDetails(username);
    }

}
