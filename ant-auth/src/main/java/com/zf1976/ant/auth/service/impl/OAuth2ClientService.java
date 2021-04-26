package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.auth.dao.ClientDetailsDao;
import com.zf1976.ant.auth.enhance.JdbcClientDetailsServiceEnhancer;
import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.ClientDetailsDTO;
import com.zf1976.ant.common.security.support.session.DistributedSessionManager;
import com.zf1976.ant.common.security.support.session.Session;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

/**
 * @author mac
 * @date 2021/4/10
 */
@Service
@SuppressWarnings("all")
public class OAuth2ClientService extends ServiceImpl<ClientDetailsDao, ClientDetails> {

    private final JdbcClientDetailsServiceEnhancer enhancer;
    private final ClientDetailsDao clientDetailsDao;

    public OAuth2ClientService(JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer, ClientDetailsDao clientDetailsDao) {
        this.enhancer = jdbcClientDetailsServiceEnhancer;
        this.clientDetailsDao = clientDetailsDao;
    }

    public IPage<ClientDetails> clientDetailsIPage(Page<ClientDetails> page) {
        return super.lambdaQuery().page(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deleteClient(String clientId) {
        final Session session = DistributedSessionManager.getSession();
        if (ObjectUtils.nullSafeEquals(clientId, session.getClientId())) {
            throw new OAuth2Exception("Prohibit deleting the currently logged in client");
        }
        if (!super.removeById(clientId)) {
            throw new OAuth2Exception(OAuth2ErrorCodes.INVALID_CLIENT);
        }
        return Optional.empty();
    }

    public Optional<Void> addClient(ClientDetailsDTO dto) {

        return Optional.empty();
    }
}
