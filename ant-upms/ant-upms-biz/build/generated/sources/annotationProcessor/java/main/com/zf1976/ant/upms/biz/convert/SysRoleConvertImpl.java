package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import com.zf1976.ant.upms.biz.pojo.vo.role.RoleVO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-05-26T23:23:18+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class SysRoleConvertImpl implements SysRoleConvert {

    @Override
    public SysRole toEntity(RoleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SysRole sysRole = new SysRole();

        sysRole.setId( dto.getId() );
        Set<Long> set = dto.getDepartmentIds();
        if ( set != null ) {
            sysRole.setDepartmentIds( new HashSet<Long>( set ) );
        }
        Set<Long> set1 = dto.getMenuIds();
        if ( set1 != null ) {
            sysRole.setMenuIds( new HashSet<Long>( set1 ) );
        }
        sysRole.setName( dto.getName() );
        sysRole.setLevel( dto.getLevel() );
        sysRole.setDescription( dto.getDescription() );
        sysRole.setEnabled( dto.getEnabled() );
        sysRole.setDataScope( dto.getDataScope() );

        return sysRole;
    }

    @Override
    public List<SysRole> toEntity(List<RoleDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<SysRole> list = new ArrayList<SysRole>( dtoList.size() );
        for ( RoleDTO roleDTO : dtoList ) {
            list.add( toEntity( roleDTO ) );
        }

        return list;
    }

    @Override
    public RoleVO toVo(SysRole entity) {
        if ( entity == null ) {
            return null;
        }

        RoleVO roleVO = new RoleVO();

        roleVO.setId( entity.getId() );
        Set<Long> set = entity.getDepartmentIds();
        if ( set != null ) {
            roleVO.setDepartmentIds( new ArrayList<Long>( set ) );
        }
        Set<Long> set1 = entity.getMenuIds();
        if ( set1 != null ) {
            roleVO.setMenuIds( new HashSet<Long>( set1 ) );
        }
        roleVO.setName( entity.getName() );
        roleVO.setLevel( entity.getLevel() );
        roleVO.setDescription( entity.getDescription() );
        roleVO.setEnabled( entity.getEnabled() );
        roleVO.setDataScope( entity.getDataScope() );
        roleVO.setCreateTime( entity.getCreateTime() );

        return roleVO;
    }

    @Override
    public List<RoleVO> toVo(List<SysRole> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<RoleVO> list = new ArrayList<RoleVO>( entityList.size() );
        for ( SysRole sysRole : entityList ) {
            list.add( toVo( sysRole ) );
        }

        return list;
    }

    @Override
    public void copyProperties(RoleDTO source, SysRole target) {
        if ( source == null ) {
            return;
        }

        target.setId( source.getId() );
        if ( target.getDepartmentIds() != null ) {
            Set<Long> set = source.getDepartmentIds();
            if ( set != null ) {
                target.getDepartmentIds().clear();
                target.getDepartmentIds().addAll( set );
            }
            else {
                target.setDepartmentIds( null );
            }
        }
        else {
            Set<Long> set = source.getDepartmentIds();
            if ( set != null ) {
                target.setDepartmentIds( new HashSet<Long>( set ) );
            }
        }
        if ( target.getMenuIds() != null ) {
            Set<Long> set1 = source.getMenuIds();
            if ( set1 != null ) {
                target.getMenuIds().clear();
                target.getMenuIds().addAll( set1 );
            }
            else {
                target.setMenuIds( null );
            }
        }
        else {
            Set<Long> set1 = source.getMenuIds();
            if ( set1 != null ) {
                target.setMenuIds( new HashSet<Long>( set1 ) );
            }
        }
        target.setName( source.getName() );
        target.setLevel( source.getLevel() );
        target.setDescription( source.getDescription() );
        target.setEnabled( source.getEnabled() );
        target.setDataScope( source.getDataScope() );
    }
}
