package com.zf1976.ant.auth.safe.filter;

import com.zf1976.ant.auth.enums.SignatureState;
import com.zf1976.ant.auth.safe.filter.signature.SignatureAuthenticationStrategy;
import com.zf1976.ant.auth.safe.filter.signature.SignaturePattern;
import com.zf1976.ant.auth.safe.filter.signature.StandardSignature;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.auth.exception.SignatureException;
import com.zf1976.ant.auth.safe.filter.signature.impl.OpenSignatureAuthenticationStrategy;
import com.zf1976.ant.auth.safe.filter.signature.impl.SecretSignatureAuthenticationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认支持两种签名认证模式
 *
 * @author mac
 * @date 2021/1/29
 **/
public class SignatureAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger LOGGER = LoggerFactory.getLogger(SignatureAuthenticationFilter.class);
    private final Map<SignaturePattern, SignatureAuthenticationStrategy> strategies;

    public SignatureAuthenticationFilter() {
        super();
        this.strategies = new ConcurrentHashMap<>(2);
        this.init();
    }

    private void init() {
        Assert.notNull(this.strategies,"signature strategy cannot been null");
        this.strategies.put(SignaturePattern.OPEN, new OpenSignatureAuthenticationStrategy());
        this.strategies.put(SignaturePattern.SECRET, new SecretSignatureAuthenticationStrategy());
    }

    private void addStrategy(SignatureAuthenticationStrategy strategy, SignaturePattern signatureModel) {
        this.strategies.put(signatureModel, strategy);
    }


    /**
     * 签名认证过滤 / 根据请求参数条件选择相应策略
     *
     * @param var1 request
     * @param var2 response
     * @param var3 filter chain
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest var1,
                                    @NonNull HttpServletResponse var2,
                                    @NonNull FilterChain var3) throws ServletException, IOException {
        // 在放行名单 直接放行
        if (SecurityContextHolder.validateUri(var1)) {
            var3.doFilter(var1, var2);
            return;
        }
        SignatureException signatureException = null;
        try {
            String signaturePattern = var1.getHeader(StandardSignature.SIGN_PATTERN);
            if (signaturePattern == null) {
                throw new SignatureException(SignatureState.NULL_PARAMS_DATA);
            }
            final SignaturePattern pattern = SignaturePattern.valueOf(signaturePattern);
            this.executeStrategy(pattern, var1);
        } catch (SignatureException e) {
            signatureException = e;
        } catch (Exception e) {
            signatureException = new SignatureException(SignatureState.ERROR, e.getMessage());
        }
        if (signatureException != null) {
            var2.setStatus(signatureException.getValue());
            SecurityContextHolder.clearContext();
            return;
        }
        var3.doFilter(var1, var2);
    }

    /**
     * 执行策略
     *
     * @param signatureModel 签名模式
     * @param request request
     */
    private void executeStrategy(SignaturePattern signatureModel, HttpServletRequest request) {
        SignatureAuthenticationStrategy strategy = this.strategies.get(signatureModel);
        Assert.notNull(strategy,"strategy cannot been null");
        SignatureException signatureException;
        try {
            if (strategy.supports(signatureModel)) {
                strategy.onAuthenticate(request);
                return;
            }
            throw new SignatureException(SignatureState.ERROR);
        } catch (SignatureException e) {
            LOGGER.error(e.getReasonPhrase(), e.getCause());
            signatureException = e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e.getCause());
            signatureException = new SignatureException(SignatureState.ERROR);
        }
        throw signatureException;
    }

}
