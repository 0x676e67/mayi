package com.zf1976.mayi.auth.convert;

import com.zf1976.mayi.auth.pojo.ClientDetails;
import com.zf1976.mayi.auth.pojo.dto.ClientDetailsDTO;
import com.zf1976.mayi.auth.pojo.vo.ClientDetailsVO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-10T13:32:28+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class ClientConvertImpl implements ClientConvert {

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
