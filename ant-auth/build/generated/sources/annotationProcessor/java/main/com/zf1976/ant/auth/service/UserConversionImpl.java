package com.zf1976.ant.auth.service;

import com.zf1976.ant.auth.pojo.vo.DepartmentVo;
import com.zf1976.ant.auth.pojo.vo.PositionVo;
import com.zf1976.ant.auth.pojo.vo.RoleVo;
import com.zf1976.ant.auth.pojo.vo.UserInfoVo;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-18T00:08:44+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class UserConversionImpl implements UserConversion {

    @Override
    public UserInfoVo convert(SysUser sysUser) {
        if ( sysUser == null ) {
            return null;
        }

        UserInfoVo userInfoVo = new UserInfoVo();

        userInfoVo.setId( sysUser.getId() );
        userInfoVo.setDepartment( sysDepartmentToDepartmentVo( sysUser.getDepartment() ) );
        userInfoVo.setRoleList( sysRoleListToRoleVoSet( sysUser.getRoleList() ) );
        userInfoVo.setPositionList( sysPositionListToPositionVoSet( sysUser.getPositionList() ) );
        userInfoVo.setUsername( sysUser.getUsername() );
        userInfoVo.setNickName( sysUser.getNickName() );
        userInfoVo.setGender( sysUser.getGender() );
        userInfoVo.setPhone( sysUser.getPhone() );
        userInfoVo.setEmail( sysUser.getEmail() );
        userInfoVo.setAvatarName( sysUser.getAvatarName() );
        userInfoVo.setAvatarPath( sysUser.getAvatarPath() );
        userInfoVo.setPassword( sysUser.getPassword() );
        userInfoVo.setEnabled( sysUser.getEnabled() );
        userInfoVo.setCreateTime( sysUser.getCreateTime() );

        return userInfoVo;
    }

    protected DepartmentVo sysDepartmentToDepartmentVo(SysDepartment sysDepartment) {
        if ( sysDepartment == null ) {
            return null;
        }

        DepartmentVo departmentVo = new DepartmentVo();

        departmentVo.setId( sysDepartment.getId() );
        departmentVo.setName( sysDepartment.getName() );

        return departmentVo;
    }

    protected RoleVo sysRoleToRoleVo(SysRole sysRole) {
        if ( sysRole == null ) {
            return null;
        }

        RoleVo roleVo = new RoleVo();

        roleVo.setId( sysRole.getId() );
        roleVo.setName( sysRole.getName() );
        roleVo.setLevel( sysRole.getLevel() );
        roleVo.setDataScope( sysRole.getDataScope() );

        return roleVo;
    }

    protected Set<RoleVo> sysRoleListToRoleVoSet(List<SysRole> list) {
        if ( list == null ) {
            return null;
        }

        Set<RoleVo> set = new HashSet<RoleVo>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( SysRole sysRole : list ) {
            set.add( sysRoleToRoleVo( sysRole ) );
        }

        return set;
    }

    protected PositionVo sysPositionToPositionVo(SysPosition sysPosition) {
        if ( sysPosition == null ) {
            return null;
        }

        PositionVo positionVo = new PositionVo();

        positionVo.setId( sysPosition.getId() );
        positionVo.setName( sysPosition.getName() );

        return positionVo;
    }

    protected Set<PositionVo> sysPositionListToPositionVoSet(List<SysPosition> list) {
        if ( list == null ) {
            return null;
        }

        Set<PositionVo> set = new HashSet<PositionVo>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( SysPosition sysPosition : list ) {
            set.add( sysPositionToPositionVo( sysPosition ) );
        }

        return set;
    }
}
