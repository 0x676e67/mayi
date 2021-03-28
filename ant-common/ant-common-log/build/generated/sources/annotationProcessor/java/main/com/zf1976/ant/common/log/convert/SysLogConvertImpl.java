package com.zf1976.ant.common.log.convert;

import com.zf1976.ant.common.log.pojo.SysLog;
import com.zf1976.ant.common.log.pojo.vo.ErrorLogVO;
import com.zf1976.ant.common.log.pojo.vo.LogVO;
import com.zf1976.ant.common.log.pojo.vo.UserLogVO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-28T18:32:12+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
public class SysLogConvertImpl implements SysLogConvert {

    @Override
    public LogVO toVo(SysLog sysLog) {
        if ( sysLog == null ) {
            return null;
        }

        LogVO logVO = new LogVO();

        logVO.setId( sysLog.getId() );
        logVO.setLogType( sysLog.getLogType() );
        logVO.setUsername( sysLog.getUsername() );
        logVO.setIp( sysLog.getIp() );
        logVO.setIpRegion( sysLog.getIpRegion() );
        logVO.setUri( sysLog.getUri() );
        logVO.setRequestMethod( sysLog.getRequestMethod() );
        logVO.setParameter( sysLog.getParameter() );
        logVO.setDescription( sysLog.getDescription() );
        logVO.setUserAgent( sysLog.getUserAgent() );
        logVO.setSpendTime( sysLog.getSpendTime() );
        logVO.setCreateTime( sysLog.getCreateTime() );

        return logVO;
    }

    @Override
    public ErrorLogVO toErrorVo(SysLog sysLog) {
        if ( sysLog == null ) {
            return null;
        }

        ErrorLogVO errorLogVO = new ErrorLogVO();

        errorLogVO.setId( sysLog.getId() );
        errorLogVO.setLogType( sysLog.getLogType() );
        errorLogVO.setUsername( sysLog.getUsername() );
        errorLogVO.setIp( sysLog.getIp() );
        errorLogVO.setIpRegion( sysLog.getIpRegion() );
        errorLogVO.setUri( sysLog.getUri() );
        errorLogVO.setRequestMethod( sysLog.getRequestMethod() );
        errorLogVO.setParameter( sysLog.getParameter() );
        errorLogVO.setDescription( sysLog.getDescription() );
        errorLogVO.setUserAgent( sysLog.getUserAgent() );
        errorLogVO.setClassName( sysLog.getClassName() );
        errorLogVO.setMethodName( sysLog.getMethodName() );
        errorLogVO.setExceptionDetails( sysLog.getExceptionDetails() );
        errorLogVO.setSpendTime( sysLog.getSpendTime() );
        errorLogVO.setCreateTime( sysLog.getCreateTime() );

        return errorLogVO;
    }

    @Override
    public UserLogVO toUserLogVo(SysLog sysLog) {
        if ( sysLog == null ) {
            return null;
        }

        UserLogVO userLogVO = new UserLogVO();

        userLogVO.setDescription( sysLog.getDescription() );
        userLogVO.setIp( sysLog.getIp() );
        userLogVO.setIpRegion( sysLog.getIpRegion() );
        userLogVO.setUserAgent( sysLog.getUserAgent() );
        userLogVO.setSpendTime( sysLog.getSpendTime() );
        userLogVO.setCreateTime( sysLog.getCreateTime() );

        return userLogVO;
    }
}
