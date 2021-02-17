package com.zf1976.ant.auth.authorize.filter;

import com.zf1976.ant.common.encrypt.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * @author ant
 * Create by Ant on 2021/2/17 8:20 PM
 */
public class PasswordDecryptFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String OAUTH_USER_PASSWORD = "password";
    private static final String OAUTH_CLIENT_SECRET = "client_secret";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        final HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getParameter(String name) {
                // 用户加密后密码
                final String password = super.getParameter(OAUTH_USER_PASSWORD);
                if (password != null) {
                    try {
                        return EncryptUtil.decryptForRsaByPrivateKey(password);
                    } catch (Exception e) {
                        printException(e);
                        return password;
                    }
                }
                final String clientSecret = super.getParameter(OAUTH_CLIENT_SECRET);
                if (clientSecret != null) {
                    try {
                        return EncryptUtil.decryptForRsaByPrivateKey(clientSecret);
                    } catch (Exception e) {
                        printException(e);
                        return clientSecret;
                    }
                }
                return super.getParameter(name);
            }
        };
        System.out.println(httpServletRequestWrapper.getParameter(OAUTH_USER_PASSWORD));
        chain.doFilter(httpServletRequestWrapper, response);
    }

    private void printException(Exception e) {
        if (log.isDebugEnabled()) {
            log.error(e.getMessage(), e.getCause());
        }
    }

}
