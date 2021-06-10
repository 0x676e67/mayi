package com.zf1976.mayi.auth.convert;

import com.zf1976.mayi.common.security.pojo.Department;
import com.zf1976.mayi.common.security.pojo.Position;
import com.zf1976.mayi.common.security.pojo.Role;
import com.zf1976.mayi.common.security.pojo.User;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import com.zf1976.mayi.upms.biz.pojo.po.SysPosition;
import com.zf1976.mayi.upms.biz.pojo.po.SysRole;
import com.zf1976.mayi.upms.biz.pojo.po.SysUser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-10T13:32:28+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class UserConvertImpl implements UserConvert {

    @Override
    public User convert(SysUser sysUser) {
        if ( sysUser == null ) {
            return null;
        }

        User user = new User();

        user.setId( sysUser.getId() );
        user.setDepartment( sysDepartmentToDepartment( sysUser.getDepartment() ) );
        user.setRoleList( sysRoleListToRoleSet( sysUser.getRoleList() ) );
        user.setPositionList( sysPositionListToPositionSet( sysUser.getPositionList() ) );
        user.setUsername( sysUser.getUsername() );
        user.setNickName( sysUser.getNickName() );
        user.setGender( sysUser.getGender() );
        user.setPhone( sysUser.getPhone() );
        user.setEmail( sysUser.getEmail() );
        user.setAvatarName( sysUser.getAvatarName() );
        user.setPassword( sysUser.getPassword() );
        user.setEnabled( sysUser.getEnabled() );
        user.setCreateTime( sysUser.getCreateTime() );

        return user;
    }

    protected Department sysDepartmentToDepartment(SysDepartment sysDepartment) {
        if ( sysDepartment == null ) {
            return null;
        }

        Department department = new Department();

        department.setId( sysDepartment.getId() );
        department.setName( sysDepartment.getName() );

        return department;
    }

    protected Role sysRoleToRole(SysRole sysRole) {
        if ( sysRole == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( sysRole.getId() );
        role.setName( sysRole.getName() );
        role.setLevel( sysRole.getLevel() );
        role.setDataScope( sysRole.getDataScope() );

        return role;
    }

    protected Set<Role> sysRoleListToRoleSet(List<SysRole> list) {
        if ( list == null ) {
            return null;
        }

        Set<Role> set = new HashSet<Role>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( SysRole sysRole : list ) {
            set.add( sysRoleToRole( sysRole ) );
        }

        return set;
    }

    protected Position sysPositionToPosition(SysPosition sysPosition) {
        if ( sysPosition == null ) {
            return null;
        }

        Position position = new Position();

        position.setId( sysPosition.getId() );
        position.setName( sysPosition.getName() );

        return position;
    }

    protected Set<Position> sysPositionListToPositionSet(List<SysPosition> list) {
        if ( list == null ) {
            return null;
        }

        Set<Position> set = new HashSet<Position>( Math.max( (int) ( list.size() / .75f ) + 1, 16 ) );
        for ( SysPosition sysPosition : list ) {
            set.add( sysPositionToPosition( sysPosition ) );
        }

        return set;
    }
}
