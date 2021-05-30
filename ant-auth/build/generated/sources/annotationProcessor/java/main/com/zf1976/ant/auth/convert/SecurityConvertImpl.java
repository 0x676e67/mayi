package com.zf1976.ant.auth.convert;

import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.dto.ClientDetailsDTO;
import com.zf1976.ant.auth.pojo.dto.PermissionDTO;
import com.zf1976.ant.auth.pojo.po.SysPermission;
import com.zf1976.ant.auth.pojo.vo.ClientDetailsVO;
import com.zf1976.ant.auth.pojo.vo.PermissionVO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-05-30T23:08:46+0800",
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
    public ClientDetailsVO toClientDetailsVO(ClientDetails clientDetails) {
        if ( clientDetails == null ) {
            return null;
        }

        ClientDetailsVO clientDetailsVO = new ClientDetailsVO();

        clientDetailsVO.setClientId( clientDetails.getClientId() );
        clientDetailsVO.setResourceIds( clientDetails.getResourceIds() );
        clientDetailsVO.setClientSecret( clientDetails.getClientSecret() );
        clientDetailsVO.setScope( clientDetails.getScope() );
        clientDetailsVO.setAuthorizedGrantTypes( clientDetails.getAuthorizedGrantTypes() );
        clientDetailsVO.setWebServerRedirectUri( clientDetails.getWebServerRedirectUri() );
        clientDetailsVO.setAuthorities( clientDetails.getAuthorities() );
        clientDetailsVO.setAccessTokenValidity( clientDetails.getAccessTokenValidity() );
        clientDetailsVO.setRefreshTokenValidity( clientDetails.getRefreshTokenValidity() );
        clientDetailsVO.setAdditionalInformation( clientDetails.getAdditionalInformation() );
        clientDetailsVO.setAutoApprove( clientDetails.getAutoApprove() );

        return clientDetailsVO;
    }

    @Override
    public ClientDetails toClientDetailsEntity(ClientDetailsDTO clientDetailsDTO) {
        if ( clientDetailsDTO == null ) {
            return null;
        }

        ClientDetails clientDetails = new ClientDetails();

        clientDetails.setClientId( clientDetailsDTO.getClientId() );
        clientDetails.setResourceIds( clientDetailsDTO.getResourceIds() );
        clientDetails.setClientSecret( clientDetailsDTO.getClientSecret() );
        clientDetails.setScope( clientDetailsDTO.getScope() );
        clientDetails.setAuthorizedGrantTypes( clientDetailsDTO.getAuthorizedGrantTypes() );
        clientDetails.setWebServerRedirectUri( clientDetailsDTO.getWebServerRedirectUri() );
        clientDetails.setAuthorities( clientDetailsDTO.getAuthorities() );
        clientDetails.setAccessTokenValidity( clientDetailsDTO.getAccessTokenValidity() );
        clientDetails.setRefreshTokenValidity( clientDetailsDTO.getRefreshTokenValidity() );
        clientDetails.setAdditionalInformation( clientDetailsDTO.getAdditionalInformation() );
        clientDetails.setAutoApprove( clientDetailsDTO.getAutoApprove() );

        return clientDetails;
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

    @Override
    public void copyProperties(ClientDetailsDTO clientDetailsDTO, ClientDetails clientDetails) {
        if ( clientDetailsDTO == null ) {
            return;
        }

        clientDetails.setClientId( clientDetailsDTO.getClientId() );
        clientDetails.setResourceIds( clientDetailsDTO.getResourceIds() );
        clientDetails.setClientSecret( clientDetailsDTO.getClientSecret() );
        clientDetails.setScope( clientDetailsDTO.getScope() );
        clientDetails.setAuthorizedGrantTypes( clientDetailsDTO.getAuthorizedGrantTypes() );
        clientDetails.setWebServerRedirectUri( clientDetailsDTO.getWebServerRedirectUri() );
        clientDetails.setAuthorities( clientDetailsDTO.getAuthorities() );
        clientDetails.setAccessTokenValidity( clientDetailsDTO.getAccessTokenValidity() );
        clientDetails.setRefreshTokenValidity( clientDetailsDTO.getRefreshTokenValidity() );
        clientDetails.setAdditionalInformation( clientDetailsDTO.getAdditionalInformation() );
        clientDetails.setAutoApprove( clientDetailsDTO.getAutoApprove() );
    }
}
