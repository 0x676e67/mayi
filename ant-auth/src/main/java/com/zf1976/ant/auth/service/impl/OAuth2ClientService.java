package com.zf1976.ant.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Splitter;
import com.zf1976.ant.auth.convert.SecurityConvert;
import com.zf1976.ant.auth.dao.ClientDetailsDao;
import com.zf1976.ant.auth.enhance.JdbcClientDetailsServiceEnhancer;
import com.zf1976.ant.auth.exception.SecurityException;
import com.zf1976.ant.auth.pojo.ClientDetails;
import com.zf1976.ant.auth.pojo.dto.ClientDetailsDTO;
import com.zf1976.ant.auth.pojo.vo.ClientDetailsVO;
import com.zf1976.ant.auth.service.AbstractSecurityService;
import com.zf1976.ant.common.component.cache.annotation.CacheConfig;
import com.zf1976.ant.common.component.cache.annotation.CacheEvict;
import com.zf1976.ant.common.component.cache.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.core.validate.Validator;
import com.zf1976.ant.common.encrypt.EncryptUtil;
import com.zf1976.ant.common.security.constant.AuthGranterTypeConstants;
import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.common.security.support.session.manager.SessionManagement;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author mac
 * @date 2021/4/10
 */
@Service
@SuppressWarnings("all")
@CacheConfig(namespace = Namespace.CLIENT)
public class OAuth2ClientService extends AbstractSecurityService<ClientDetailsDao, ClientDetails> {

    private static final Pattern ID_SECRET_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{10,32}$");
    private final int tokenMinTime = 3600;
    private final int tokenRefreshMinTime = 7200;
    private final int tokenMaxTime = 2678400;
    private final int tokenRefreshMaxTime = 5356800;
    private final JdbcClientDetailsServiceEnhancer enhancer;
    private final ClientDetailsDao clientDetailsDao;
    private final SecurityConvert convert;

    public OAuth2ClientService(JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer,
                               ClientDetailsDao clientDetailsDao) {
        this.convert = SecurityConvert.INSTANCE;
        this.enhancer = jdbcClientDetailsServiceEnhancer;
        this.clientDetailsDao = clientDetailsDao;
    }

    public static void main(String[] args) {

    }

    /**
     * 分页查询
     *
     * @param query 查询对象
     * @return {@link IPage< ClientDetailsVO>}
     * @throws
     */
    @CachePut(key = "#query")
    public IPage<ClientDetailsVO> clientDetailsPage(Query<?> query) {
        Page<ClientDetails> sourcePage = super.lambdaQuery()
                                              .page(query.toPage());
        return super.mapToTarget(sourcePage, convert::toClientDetailsVo);
    }

    /**
     * 新增客户端
     *
     * @param dto DTO
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void addClient(ClientDetailsDTO dto) {
        // 校验客户端ID，Secret是否合格
        Validator.of(dto)
                 // 校验客户端ID是否合格
                 .withValidated(data -> this.validateIdAndSecret(data.getClientId()),
                         () -> new SecurityException("ID does not meet the requirements"))
                 // 校验客户端密钥是否合格
                 .withValidated(data -> this.validateIdAndSecret(data.getClientSecret()),
                         () -> new SecurityException("Secret does not meet the requirements"))
                 // 校验token有效时间范围
                 .withValidated(data -> this.validateTokenTimeScope(data.getAccessTokenValidity()),
                         () -> new SecurityException("The token validity time does not meet the requirements"))
                 // 校验refresh token有效时间范围
                 .withValidated(data -> this.validateRefreshTokenTimeScope(data.getRefreshTokenValidity()),
                         () -> new SecurityException("The refresh token validity time does not meet the requirements"))
                 // 校验认证模式
                 .withValidated(data -> this.ValidateGranterType(data.getAuthorizedGrantTypes()),
                         () -> new SecurityException("The certification model does not meet the requirements"))
                 // 校验权限范围
                 .withValidated(data -> data.getScope()
                                            .equals("all"),
                         () -> new SecurityException("The scope of authority does not meet the requirements"));
        ClientDetails clientDetails = this.convert.toClientDetailsEntity(dto);
        try {
            // 加密明文密码
            clientDetails.setRawClientSecret(EncryptUtil.encryptForRsaByPublicKey(dto.getClientSecret()));
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            throw new SecurityException("Operation failed");
        }
        // 新增数据
        if (!super.saveOrUpdate(clientDetails)) {
            throw new SecurityException("Failed to insert client data");
        }
        return null;
    }

    /**
     * 正则校验客户端ID、Secret
     *
     * @param value
     * @return {@link boolean}
     * @throws
     */
    private boolean validateIdAndSecret(String value) {
        return ID_SECRET_PATTERN.matcher(value)
                                .find();
    }

    /**
     * 校验refresh token有效时间范围
     *
     * @param value 值
     * @return {@link boolean}
     * @throws
     */
    private boolean validateRefreshTokenTimeScope(Integer value) {
        if (value != null) {
            return value >= this.tokenRefreshMinTime && value <= this.tokenRefreshMaxTime;
        }
        return false;
    }

    /**
     * 校验token有效时间范围
     *
     * @param value 值
     * @return
     */
    private boolean validateTokenTimeScope(Integer value) {
        if (value != null) {
            return value >= this.tokenMinTime && value <= this.tokenMaxTime;
        }
        return false;
    }

    private boolean ValidateGranterType(String granterTypes) {
        Iterable<String> split = Splitter.on(",")
                                         .trimResults()
                                         .omitEmptyStrings()
                                         .split(granterTypes);
        for (String grantType : split) {
            // 试图匹配系统所有认证模式
            boolean isMatch = false;
            // 循环校验，最终isMatch为false，退出
            for (String type : AuthGranterTypeConstants.ARRAY) {
                // 匹配成功break
                if (type.equals(grantType)) {
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除OAuth客户端
     *
     * @param clientId 客户端id
     * @return {@link Optional< Void>}
     * @date 2021-05-05 19:48:08
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void deleteClient(String clientId) {
        final Session session = SessionManagement.getSession();
        if (ObjectUtils.nullSafeEquals(clientId, session.getClientId())) {
            throw new OAuth2Exception("Prohibit deleting the currently logged in client");
        }
        if (!super.removeById(clientId)) {
            throw new OAuth2Exception(OAuth2ErrorCodes.INVALID_CLIENT);
        }
        return null;
    }

}
