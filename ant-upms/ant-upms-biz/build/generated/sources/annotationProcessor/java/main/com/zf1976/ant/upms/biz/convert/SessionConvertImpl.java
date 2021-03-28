package com.zf1976.ant.upms.biz.convert;

import com.zf1976.ant.common.component.session.Session;
import com.zf1976.ant.upms.biz.pojo.vo.SessionVO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T20:43:23+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
public class SessionConvertImpl implements SessionConvert {

    @Override
    public SessionVO toVO(Session session) {
        if ( session == null ) {
            return null;
        }

        SessionVO sessionVO = new SessionVO();

        sessionVO.setId( session.getId() );
        sessionVO.setUsername( session.getUsername() );
        sessionVO.setNickName( session.getNickName() );
        sessionVO.setDepartment( session.getDepartment() );
        sessionVO.setIp( session.getIp() );
        sessionVO.setIpRegion( session.getIpRegion() );
        sessionVO.setBrowser( session.getBrowser() );
        sessionVO.setLoginTime( session.getLoginTime() );

        return sessionVO;
    }
}
