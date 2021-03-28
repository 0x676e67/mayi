package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.ant.upms.biz.pojo.vo.user.UserVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T20:43:23+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
public class SysUserConvertImpl implements SysUserConvert {

    @Override
    public SysUser toEntity(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysUser sysUser = new SysUser();

        sysUser.setId( dto.getId() );
        sysUser.setDepartmentId( dto.getDepartmentId() );
        sysUser.setUsername( dto.getUsername() );
        sysUser.setNickName( dto.getNickName() );
        sysUser.setGender( dto.getGender() );
        sysUser.setPhone( dto.getPhone() );
        sysUser.setEmail( dto.getEmail() );
        sysUser.setEnabled( dto.getEnabled() );

        return sysUser;
    }

    @Override
    public List<SysUser> toEntity(List<UserDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysUser> list = new ArrayList<SysUser>( dtoList.size() );
        for ( UserDTO userDTO : dtoList ) {
            list.add( toEntity( userDTO ) );
        }

        return list;
    }

    @Override
    public UserVO toVo(SysUser entity) {
        if ( entity == null ) {
            return null;
        }

        UserVO userVO = new UserVO();

        userVO.setId( entity.getId() );
        userVO.setDepartment( sysDepartmentToDepartmentVO( entity.getDepartment() ) );
        userVO.setAvatarName( entity.getAvatarName() );
        userVO.setUsername( entity.getUsername() );
        userVO.setNickName( entity.getNickName() );
        userVO.setGender( entity.getGender() );
        userVO.setPhone( entity.getPhone() );
        userVO.setEmail( entity.getEmail() );
        userVO.setEnabled( entity.getEnabled() );
        userVO.setCreateTime( entity.getCreateTime() );

        return userVO;
    }

    @Override
    public List<UserVO> toVo(List<SysUser> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<UserVO> list = new ArrayList<UserVO>( entityList.size() );
        for ( SysUser sysUser : entityList ) {
            list.add( toVo( sysUser ) );
        }

        return list;
    }

    @Override
    public void copyProperties(UserDTO source, SysUser target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        target.setDepartmentId( source.getDepartmentId() );
        target.setUsername( source.getUsername() );
        target.setNickName( source.getNickName() );
        target.setGender( source.getGender() );
        target.setPhone( source.getPhone() );
        target.setEmail( source.getEmail() );
        target.setEnabled( source.getEnabled() );
    }

    @Override
    public UserVO clone(UserVO source, UserVO target) {
        if ( source == null ) {
            return null;
        }

        target.setId( source.getId() );
        target.setDepartment( source.getDepartment() );
        target.setAvatarName( source.getAvatarName() );
        target.setUsername( source.getUsername() );
        target.setNickName( source.getNickName() );
        target.setGender( source.getGender() );
        target.setPhone( source.getPhone() );
        target.setEmail( source.getEmail() );
        target.setEnabled( source.getEnabled() );
        target.setCreateTime( source.getCreateTime() );

        return target;
    }

    protected DepartmentVO sysDepartmentToDepartmentVO(SysDepartment sysDepartment) {
        if ( sysDepartment == null ) {
            return null;
        }

        DepartmentVO departmentVO = new DepartmentVO();

        departmentVO.setId( sysDepartment.getId() );
        departmentVO.setPid( sysDepartment.getPid() );
        departmentVO.setName( sysDepartment.getName() );
        departmentVO.setDeptSort( sysDepartment.getDeptSort() );
        departmentVO.setEnabled( sysDepartment.getEnabled() );
        departmentVO.setCreateTime( sysDepartment.getCreateTime() );

        return departmentVO;
    }
}
