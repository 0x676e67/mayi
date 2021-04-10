package com.zf1976.ant.auth.enhance;

import com.zf1976.ant.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.zf1976.ant.common.security.support.signature.datasource.domain.Client;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.util.JdbcListFactory;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author mac
 * @date 2021/3/5
 **/
@Service
public class JdbcClientDetailsServiceEnhancer extends JdbcClientDetailsService implements ClientDataSourceProvider {

    public JdbcClientDetailsServiceEnhancer(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @CachePut(namespace = Namespace.CLIENT, key = "#clientId")
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        return super.loadClientByClientId(clientId);
    }

    @Override
    @CachePut(namespace = Namespace.CLIENT, key = "client_list")
    public List<ClientDetails> listClientDetails() {
        return super.listClientDetails();
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        super.addClientDetails(clientDetails);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        super.removeClientDetails(clientId);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        super.updateClientDetails(clientDetails);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        super.updateClientSecret(clientId, secret);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setListFactory(JdbcListFactory listFactory) {
        super.setListFactory(listFactory);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setUpdateClientDetailsSql(String updateClientDetailsSql) {
        super.setUpdateClientDetailsSql(updateClientDetailsSql);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setUpdateClientSecretSql(String updateClientSecretSql) {
        super.setUpdateClientSecretSql(updateClientSecretSql);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setInsertClientDetailsSql(String insertClientDetailsSql) {
        super.setInsertClientDetailsSql(insertClientDetailsSql);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setFindClientDetailsSql(String findClientDetailsSql) {
        super.setFindClientDetailsSql(findClientDetailsSql);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setDeleteClientDetailsSql(String deleteClientDetailsSql) {
        super.setDeleteClientDetailsSql(deleteClientDetailsSql);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setSelectClientDetailsSql(String selectClientDetailsSql) {
        super.setSelectClientDetailsSql(selectClientDetailsSql);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    @CacheEvict(namespace = Namespace.CLIENT)
    public void setRowMapper(RowMapper<ClientDetails> rowMapper) {
        super.setRowMapper(rowMapper);
    }

    @Override
    public Client selectClientByClientId(String clientId) {
        // 查询客户端
        var clientDetails = this.loadClientByClientId(clientId);
        var client = new Client();
        if (clientDetails != null) {
            client.setClientId(clientDetails.getClientId())
                  .setClientSecret(clientDetails.getClientSecret());
        }
        return client;
    }
}
