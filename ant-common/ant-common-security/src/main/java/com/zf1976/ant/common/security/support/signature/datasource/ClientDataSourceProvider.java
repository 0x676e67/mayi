package com.zf1976.ant.common.security.support.signature.datasource;

import com.zf1976.ant.common.security.support.signature.datasource.domain.Client;

/**
 * 客户端数据源接口
 *
 * @author mac
 * @date 2021/3/24
 **/
public interface ClientDataSourceProvider {

    /**
     * 根据客户端id查找客户端
     *
     * @param clientId client id
     * @return {@link Client}
     */
    Client selectClientByClientId(String clientId);
}
