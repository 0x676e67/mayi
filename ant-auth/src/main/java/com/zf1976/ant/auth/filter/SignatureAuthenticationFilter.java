package com.zf1976.ant.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.ant.common.core.foundation.ResultData;
import com.zf1976.ant.common.security.SecurityContextHolder;
import com.zf1976.ant.common.security.enums.SignatureState;
import com.zf1976.ant.common.security.exception.SignatureException;
import com.zf1976.ant.auth.filter.support.SignatureAuthenticationStrategy;
import com.zf1976.ant.auth.filter.support.SignaturePattern;
import com.zf1976.ant.auth.filter.support.StandardSignature;
import com.zf1976.ant.auth.filter.support.impl.OpenSignatureAuthenticationStrategy;
import com.zf1976.ant.auth.filter.support.impl.SecretSignatureAuthenticationStrategy;
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
    private final Map<SignaturePattern, SignatureAuthenticationStrategy> strategies = new ConcurrentHashMap<>(2);
    private final ObjectMapper objectMapper = new ObjectMapper();
    public SignatureAuthenticationFilter() {
        super();
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
     * @param request request
     * @param response response
     * @param filterChain filter chain
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 在放行名单 直接放行
        if (SecurityContextHolder.validateUri(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String signaturePattern;
        signaturePattern = request.getHeader(StandardSignature.SIGN_PATTERN);
        if (signaturePattern == null) {
            this.handlerException(response, new SignatureException(SignatureState.NULL_PARAMS_DATA));
            SecurityContextHolder.clearContext();
            return;
        }
        try {
            final SignaturePattern pattern = SignaturePattern.valueOf(signaturePattern);
            this.executeStrategy(pattern, request);
        } catch (Exception e) {
            this.handlerException(response, new SignatureException(SignatureState.ERROR, e.getMessage()));
            SecurityContextHolder.clearContext();
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void handlerException(HttpServletResponse response, SignatureException signatureException) {
        response.setStatus(signatureException.getValue());
        try {
            this.objectMapper.writeValue(response.getOutputStream(), ResultData.fail(signatureException.getReasonPhrase()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e.getCause());
        }
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
