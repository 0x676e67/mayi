package com.zf1976.ant.auth.service;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.auth.pojo.vo.RoleVo;
import com.zf1976.ant.auth.pojo.vo.UserInfoVo;
import com.zf1976.ant.auth.enums.AuthenticationState;
import com.zf1976.ant.auth.exception.UserNotFountException;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.auth.LoginUserDetails;
import com.zf1976.ant.upms.biz.dao.*;
import com.zf1976.ant.upms.biz.pojo.po.*;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@Service("securityUserDetailsService")
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    private final SecurityProperties securityConfig;
    private final SysDepartmentDao sysDepartmentDao;
    private final SysUserDao sysUserDao;
    private final SysRoleDao sysRoleDao;
    private final SysMenuDao sysMenuDao;
    private final SysPositionDao positionDao;
    private final UserConversion convert;

    public SecurityUserDetailsServiceImpl(SecurityProperties securityConfig,
                                          SysDepartmentDao sysDepartmentDao,
                                          SysUserDao sysUserDao,
                                          SysRoleDao sysRoleDao,
                                          SysMenuDao sysMenuDao,
                                          SysPositionDao positionDao) {
        this.sysUserDao = sysUserDao;
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysRoleDao = sysRoleDao;
        this.sysMenuDao = sysMenuDao;
        this.securityConfig = securityConfig;
        this.positionDao = positionDao;
        this.convert = UserConversion.INSTANCE;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        LoginUserDetails securityUserDetails;
        // 初步验证用户是否存在
        SysUser user = ChainWrappers.lambdaQueryChain(this.sysUserDao)
                                    .eq(SysUser::getUsername, username)
                                    .oneOpt()
                                    .orElseThrow(() -> new UserNotFountException(AuthenticationState.USER_NOT_FOUNT));
        // 查询用户部门
        SysDepartment department = ChainWrappers.lambdaQueryChain(this.sysDepartmentDao)
                                                .eq(SysDepartment::getId, user.getDepartmentId())
                                                .one();
        // 查询用户角色
        List<SysRole> roleList = this.sysRoleDao.selectListByUserId(user.getId());
        // 查询用户职位
        List<SysPosition> positionList = this.positionDao.selectListByUserId(user.getId());
        user.setDepartment(department);
        user.setRoleList(roleList);
        user.setPositionList(positionList);
        final UserInfoVo userInfoVo = Optional.ofNullable(convert.convert(user))
                                              .orElseThrow(() -> new UserNotFountException(AuthenticationState.USER_NOT_FOUNT));
        if (!userInfoVo.getEnabled()) {
            throw new UserNotFountException(AuthenticationState.ACCOUNT_DISABLED);
        } else {
            securityUserDetails = new LoginUserDetails(
                    userInfoVo,
                    this.grantedAuthorities(userInfoVo),
                    this.getDataPermission(userInfoVo));
        }
        return securityUserDetails;
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
    private Set<Long> getDataPermission(UserInfoVo userInfo) {

        // 超级管理员
        if (ObjectUtils.nullSafeEquals(userInfo.getUsername(), this.securityConfig.getAdmin())) {
            Set<Long> allDataPermission = this.sysDepartmentDao.selectList(null)
                                                               .stream()
                                                               .map(SysDepartment::getId)
                                                               .collect(Collectors.toSet());
            return Collections.unmodifiableSet(allDataPermission);
        }

        // 用户级别角色排序
        final Set<RoleVo> roles = userInfo.getRoleList()
                                          .stream()
                                          .sorted(Comparator.comparingInt(RoleVo::getLevel))
                                          .collect(Collectors.toCollection(LinkedHashSet::new));
        // 数据权限
        final Set<Long> dataPermission = new HashSet<>();

        for (RoleVo roleInfo : roles) {
            switch (Objects.requireNonNull(roleInfo.getDataScope())) {
                case LEVEL:
                    // 本级数据数据权限 用户部门
                    dataPermission.add(userInfo.getDepartment().getId());
                    break;

                case CUSTOMIZE:
                    // 自定义用户/角色所在部门的数据权限
                    sysDepartmentDao.selectListByRoleId(roleInfo.getId())
                                    .stream()
                                    .map(SysDepartment::getId)
                                    .forEach(id -> {
                                  this.collectDepartmentTreeIds(id, () -> dataPermission);
                              });
                    dataPermission.add(userInfo.getDepartment().getId());
                    break;

                default:
                    // 所有数据权限
                    Set<Long> allDataPermission = this.sysDepartmentDao.selectList(null)
                                                                       .stream()
                                                                       .map(SysDepartment::getId)
                                                                       .collect(Collectors.toSet());
                    return Collections.unmodifiableSet(allDataPermission);
            }
        }
        return Collections.unmodifiableSet(dataPermission);
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
    private List<GrantedAuthority> grantedAuthorities(UserInfoVo userInfo) {
        Set<String> authorities = new HashSet<>();
        String markerAdmin = ApplicationConfigUtils.getSecurityProperties().getAdmin();
        if (userInfo.getUsername().equals(markerAdmin)) {
            authorities.add(securityConfig.getAdmin());
            return authorities.stream()
                              .map(SimpleGrantedAuthority::new)
                              .collect(Collectors.toList());
        } else {
            List<SysRole> sysRoles = sysRoleDao.selectListByUserId(userInfo.getId());
            authorities = sysRoles.stream()
                                  .flatMap(sysRole -> sysMenuDao.selectListByRoleId(sysRole.getId())
                                                                .stream()
                                          )
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

}
