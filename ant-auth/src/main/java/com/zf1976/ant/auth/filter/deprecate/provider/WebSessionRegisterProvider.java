package com.zf1976.ant.auth.filter.deprecate.provider;

import com.zf1976.ant.auth.LoginUserDetails;
import com.zf1976.ant.auth.JwtTokenProvider;
import com.zf1976.ant.common.security.support.session.DistributedSessionManager;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author mac
 * Create by Ant on 2020/10/3 15:09
 */
@Deprecated
public class WebSessionRegisterProvider implements AuthenticationProvider {

    private static final SecurityProperties CONFIG = SpringContextHolder.getBean(SecurityProperties.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String token = JwtTokenProvider.createToken(authentication);
        final LoginUserDetails userDetails = (LoginUserDetails) authentication.getDetails();
        //校验登陆状态 踢下线
        if (CONFIG.getTokenSingle()) {
            DistributedSessionManager.removeSession(userDetails.getId());
        }
        UsernamePasswordAuthenticationToken authenticationToken;
        authenticationToken = new UsernamePasswordAuthenticationToken(authentication.getName(),
                                                                      token,
                                                                      authentication.getAuthorities());
        authenticationToken.setDetails(userDetails);
        return authenticationToken;
    }

    /**
     * 表明这provider可以处理身份验证请求。因为目前只有一个登录,总是返回true
     *
     * @param aClass 类对象
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
