package com.zf1976.mayi.upms.biz.security.convert;

import com.zf1976.mayi.upms.biz.pojo.dto.PermissionDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysPermission;
import com.zf1976.mayi.upms.biz.pojo.vo.PermissionVO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-10T14:08:13+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class SecurityConvertImpl implements SecurityConvert {

    @Override
    public PermissionVO toPermissionVO(SysPermission sysPermission) {
        if ( sysPermission == null ) {
            return null;
        }

        PermissionVO permissionVO = new PermissionVO();

        permissionVO.setId( sysPermission.getId() );
        permissionVO.setName( sysPermission.getName() );
        permissionVO.setValue( sysPermission.getValue() );
        permissionVO.setDescription( sysPermission.getDescription() );
        permissionVO.setCreateBy( sysPermission.getCreateBy() );

        return permissionVO;
    }

    @Override
    public SysPermission toPermissionEntity(PermissionDTO permissionDTO) {
        if ( permissionDTO == null ) {
            return null;
        }

        SysPermission sysPermission = new SysPermission();

        sysPermission.setId( permissionDTO.getId() );
        sysPermission.setName( permissionDTO.getName() );
        sysPermission.setValue( permissionDTO.getValue() );
        sysPermission.setDescription( permissionDTO.getDescription() );

        return sysPermission;
    }

    @Override
    public void copyProperties(PermissionDTO permissionDTO, SysPermission sysPermission) {
        if ( permissionDTO == null ) {
            return;
        }

        sysPermission.setId( permissionDTO.getId() );
        sysPermission.setName( permissionDTO.getName() );
        sysPermission.setValue( permissionDTO.getValue() );
        sysPermission.setDescription( permissionDTO.getDescription() );
    }
}
