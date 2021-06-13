package com.zf1976.mayi.auth.service;

import com.zf1976.mayi.auth.LoginUserDetails;
import com.zf1976.mayi.auth.exception.UserNotFountException;
import com.zf1976.mayi.auth.RemoteUserService;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.constants.SecurityConstants;
import com.zf1976.mayi.common.security.enums.AuthenticationState;
import com.zf1976.mayi.upms.biz.pojo.Details;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:02
 */
@Service("userDetailsService")
public class OAuth2UserDetailsService implements UserDetailsServiceEnhancer {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final RemoteUserService remoteUserService;
    private final DiscoveryClient discoveryClient;

    public OAuth2UserDetailsService(RemoteUserService remoteUserService,
                                    DiscoveryClient discoveryClient) {
        this.remoteUserService = remoteUserService;
        this.discoveryClient = discoveryClient;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        DataResult<User> userDataResult = this.remoteUserService.getUser(username, SecurityConstants.FROM_IN);
        User user = userDataResult.getData();
        if (user == null) {
            throw new UserNotFountException(AuthenticationState.USER_NOT_FOUNT);
        }
        if (!user.getEnabled()) {
            throw new LockedException(this.messages.getMessage("AccountStatusUserDetailsChecker.locked", "User account is locked"));
        }
        return new LoginUserDetails(user);
    }



    /**
     * 查询用户细节
     *
     * @param username 用户名
     * @return {@link Details}
     */
    @Override
    public Details selectUserDetails(String username) {
        LoginUserDetails userDetails = (LoginUserDetails) this.loadUserByUsername(username);
        return new Details(userDetails.getPermission(), userDetails.getDataPermission(), userDetails.getDelegate());
    }


    @Override
    @CachePut(namespace = Namespace.USER, dynamics = true)
    public Details selectUserDetails() {
        final String username = SessionManagement.getCurrentUsername();
        return this.selectUserDetails(username);
    }



}
