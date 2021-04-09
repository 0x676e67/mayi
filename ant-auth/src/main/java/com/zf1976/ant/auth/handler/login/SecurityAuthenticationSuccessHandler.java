package com.zf1976.ant.auth.handler.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.common.util.Base64Util;
import com.zf1976.ant.auth.LoginResponse;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.encrypt.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ant
 * Create by Ant on 2020/9/12 10:03 上午
 */
@Deprecated
public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * 登陆成功处理
     *
     * @param httpServletRequest request
     * @param httpServletResponse response
     * @param authentication 认证对象
     * @throws IOException 向上抛异常
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Assert.isInstanceOf(UserDetails.class, authentication.getDetails());
        final UserDetails userDetails = (UserDetails) authentication.getDetails();
        final String token = (String) authentication.getCredentials();
        final LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token).setUser(userDetails);
        // 创建session
        SecurityContextHolder.createSession(token);
        // 原始content
        String rawContent = jsonMapper.writeValueAsString(DataResult.success(loginResponse));
        // 加密后内容
        String result = EncryptUtil.encryptForAesByCbc(rawContent);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        jsonMapper.writeValue(httpServletResponse.getOutputStream(), Base64Util.encryptToString(result));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
