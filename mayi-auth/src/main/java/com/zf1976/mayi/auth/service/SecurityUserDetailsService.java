package com.zf1976.mayi.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.mayi.auth.LoginUserDetails;
import com.zf1976.mayi.auth.convert.UserConvert;
import com.zf1976.mayi.auth.dao.SysPermissionDao;
import com.zf1976.mayi.auth.exception.UserNotFountException;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.common.security.enums.AuthenticationState;
import com.zf1976.mayi.common.security.pojo.Details;
import com.zf1976.mayi.common.security.pojo.Role;
import com.zf1976.mayi.common.security.pojo.User;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import com.zf1976.mayi.upms.biz.dao.*;
import com.zf1976.mayi.upms.biz.pojo.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:02
 */
@Service("userDetailsService")
public class SecurityUserDetailsService implements UserDetailsServiceEnhancer {

    private SecurityProperties securityProperties;
    private SysDepartmentDao departmentDao;
    private SysUserDao userDao;
    private SysRoleDao roleDao;
    private SysMenuDao menuDao;
    private SysPositionDao positionDao;
    private SysPermissionDao permissionDao;
    private final UserConvert convert = UserConvert.INSTANCE;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        final User user = this.findUserByUsername(username);
        if (!user.getEnabled()) {
            throw new UserNotFountException(AuthenticationState.ACCOUNT_DISABLED);
        }
        // 权限值
        List<GrantedAuthority> grantedAuthorities = this.grantedAuthorities(user);
        // 数据权限
        Set<Long> grantedDataPermission = this.grantedDataPermission(user);
        return new LoginUserDetails(user, grantedAuthorities, grantedDataPermission);
    }

    private User findUserByUsername(String username) {
        // 初步验证用户是否存在
        SysUser sysUser = ChainWrappers.lambdaQueryChain(this.userDao)
                                       .eq(SysUser::getUsername, username)
                                       .oneOpt()
                                       .orElseThrow(() -> new UserNotFountException(AuthenticationState.USER_NOT_FOUNT));
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

        return this.convert.convert(sysUser);
    }

    /**
     * 获取用户数据权限
     * 实际上根据部门id作为数据范围值
     *
     * @param user 用户信息
     * @return 数据权限
     */
    private Set<Long> grantedDataPermission(User user) {

        // 超级管理员
        if (ObjectUtils.nullSafeEquals(user.getUsername(), this.securityProperties.getOwner())) {
            return this.departmentDao.selectList(Wrappers.emptyWrapper())
                                     .stream()
                                     .map(SysDepartment::getId)
                                     .collect(Collectors.toSet());
        }

        // 用户级别角色排序
        final Set<Role> roles = user.getRoleList()
                                        .stream()
                                        .sorted(Comparator.comparingInt(Role::getLevel))
                                        .collect(Collectors.toCollection(LinkedHashSet::new));
        // 数据权限范围
        final Set<Long> dataPermission = new HashSet<>();
        for (Role role : roles) {
            switch (Objects.requireNonNull(role.getDataScope())) {
                case LEVEL:
                    // 本级数据权限 用户部门
                    dataPermission.add(user.getDepartment().getId());
                    break;

                case CUSTOMIZE:
                    // 自定义用户/角色所在部门的数据权限
                    departmentDao.selectListByRoleId(role.getId())
                                 .stream()
                                 .map(SysDepartment::getId)
                                 .forEach(id -> {
                                  this.collectDepartmentTreeIds(id, dataPermission);
                              });
                    dataPermission.add(user.getDepartment().getId());
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
    public void collectDepartmentTreeIds(Long departmentId, Set<Long> handler) {
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
     * @param user info
     * @return 返回用户权限信息
     */
    private List<GrantedAuthority> grantedAuthorities(User user) {
        Set<String> authorities = new HashSet<>();
        String markerAdmin = securityProperties.getOwner();
        if (user.getUsername().equals(markerAdmin)) {
            // 分配认证超级管理员角色
            authorities.add("ROLE_" + markerAdmin);
            return authorities.stream()
                              .map(SimpleGrantedAuthority::new)
                              .collect(Collectors.toList());
        } else {
            List<SysRole> roles = roleDao.selectBatchByUserId(user.getId());
            authorities = roles.stream()
                                  .flatMap(role -> menuDao.selectListByRoleId(role.getId()).stream())
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

    /**
     * 查询用户细节
     *
     * @param username 用户名
     * @return {@link Details}
     */
    @Override
    public Details selectUserDetails(String username) {
        LoginUserDetails userDetails = (LoginUserDetails) this.loadUserByUsername(username);
        return new Details(userDetails.getPermission(), userDetails.getDataPermission(), userDetails.getUser());
    }


    @Override
    @CachePut(namespace = Namespace.USER, dynamics = true)
    public Details selectUserDetails() {
        final String username = SessionManagement.getCurrentUsername();
        return this.selectUserDetails(username);
    }

    @Autowired
    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Autowired
    public void setDepartmentDao(SysDepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Autowired
    public void setUserDao(SysUserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setRoleDao(SysRoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Autowired
    public void setMenuDao(SysMenuDao menuDao) {
        this.menuDao = menuDao;
    }

    @Autowired
    public void setPositionDao(SysPositionDao positionDao) {
        this.positionDao = positionDao;
    }

    @Autowired
    public void setPermissionDao(SysPermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

}
