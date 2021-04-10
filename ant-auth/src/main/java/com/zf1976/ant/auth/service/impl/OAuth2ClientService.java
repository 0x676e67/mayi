package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.auth.dao.ClientDetailsDao;
import com.zf1976.ant.auth.enhance.JdbcClientDetailsServiceEnhancer;
import com.zf1976.ant.auth.pojo.ClientDetails;
import org.springframework.stereotype.Service;

/**
 * @author mac
 * @date 2021/4/10
 */
@Service
@SuppressWarnings("all")
public class OAuth2ClientService extends ServiceImpl<ClientDetailsDao, ClientDetails> {

    private final JdbcClientDetailsServiceEnhancer enhancer;
    private final ClientDetailsDao clientDetailsDao;

    public OAuth2ClientService(JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer,
                               ClientDetailsDao clientDetailsDao) {
        this.enhancer = jdbcClientDetailsServiceEnhancer;
        this.clientDetailsDao = clientDetailsDao;
    }

    public IPage<ClientDetails> clientDetailsIPage(Page<ClientDetails> page) {
        return super.lambdaQuery().page(page);
    }


}
