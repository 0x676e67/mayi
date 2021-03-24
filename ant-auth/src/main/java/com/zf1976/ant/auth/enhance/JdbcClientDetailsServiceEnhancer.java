package com.zf1976.ant.auth.enhance;

import com.zf1976.ant.common.security.support.datasource.ClientDataSourceProvider;
import com.zf1976.ant.common.security.support.datasource.domain.Client;
import com.zf1976.ant.common.component.load.annotation.CaffeineEvict;
import com.zf1976.ant.common.component.load.annotation.CaffeinePut;
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
    @CaffeinePut(namespace = Namespace.CLIENT, key = "#clientId")
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        return super.loadClientByClientId(clientId);
    }

    @Override
    @CaffeinePut(namespace = Namespace.CLIENT, key = "client_list")
    public List<ClientDetails> listClientDetails() {
        return super.listClientDetails();
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        super.addClientDetails(clientDetails);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        super.removeClientDetails(clientId);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        super.updateClientDetails(clientDetails);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        super.updateClientSecret(clientId, secret);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setListFactory(JdbcListFactory listFactory) {
        super.setListFactory(listFactory);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setUpdateClientDetailsSql(String updateClientDetailsSql) {
        super.setUpdateClientDetailsSql(updateClientDetailsSql);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setUpdateClientSecretSql(String updateClientSecretSql) {
        super.setUpdateClientSecretSql(updateClientSecretSql);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setInsertClientDetailsSql(String insertClientDetailsSql) {
        super.setInsertClientDetailsSql(insertClientDetailsSql);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setFindClientDetailsSql(String findClientDetailsSql) {
        super.setFindClientDetailsSql(findClientDetailsSql);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setDeleteClientDetailsSql(String deleteClientDetailsSql) {
        super.setDeleteClientDetailsSql(deleteClientDetailsSql);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setSelectClientDetailsSql(String selectClientDetailsSql) {
        super.setSelectClientDetailsSql(selectClientDetailsSql);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
    }

    @Override
    @CaffeineEvict(namespace = Namespace.CLIENT)
    public void setRowMapper(RowMapper<ClientDetails> rowMapper) {
        super.setRowMapper(rowMapper);
    }

    @Override
    public Client selectClientByClientId(String clientId) {
        var clientDetails = this.loadClientByClientId(clientId);
        var client = new Client();
        if (clientDetails != null) {
            client.setClientId(clientDetails.getClientId())
                  .setClientSecret(clientDetails.getClientSecret());
        }
        return client;
    }
}
