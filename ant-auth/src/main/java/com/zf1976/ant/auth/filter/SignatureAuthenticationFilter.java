package com.zf1976.ant.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.ant.auth.SecurityContextHolder;
import com.zf1976.ant.common.core.util.JSONUtil;
import com.zf1976.ant.common.security.support.signature.exception.SignatureException;
import com.zf1976.ant.common.security.support.signature.SignatureAuthenticationStrategy;
import com.zf1976.ant.common.security.support.signature.enums.SignaturePattern;
import com.zf1976.ant.common.security.support.signature.StandardSignature;
import com.zf1976.ant.common.security.support.signature.datasource.ClientDataSourceProvider;
import com.zf1976.ant.common.security.support.signature.impl.OpenSignatureAuthenticationStrategy;
import com.zf1976.ant.common.security.support.signature.impl.SecretSignatureAuthenticationStrategy;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.security.support.signature.SignatureState;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private final Set<String> ignoredSet = new HashSet<>();
    public SignatureAuthenticationFilter(ClientDataSourceProvider provider, String ...ignoredUri) {
        super();
        Assert.notNull(ignoredUri, "ignored uri cannot benn null");
        final Set<String> set = Arrays.stream(ignoredUri)
                                      .collect(Collectors.toSet());
        ignoredSet.addAll(set);
        this.init(provider);
    }

    private void init(ClientDataSourceProvider provider) {
        Assert.notNull(this.strategies,"signature strategy cannot been null");
        this.strategies.put(SignaturePattern.OPEN, new OpenSignatureAuthenticationStrategy(provider));
        this.strategies.put(SignaturePattern.SECRET, new SecretSignatureAuthenticationStrategy(provider));
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
        // 忽略uri
        if (ignoredSet.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            this.executeStrategy(request);
        } catch (SignatureException e) {
            this.handlerException(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private SignaturePattern extractSignaturePattern(HttpServletRequest request) {
        String signaturePattern;
        signaturePattern = request.getHeader(StandardSignature.SIGN_PATTERN);
        if (signaturePattern == null) {
            SecurityContextHolder.clearContext();
            throw new SignatureException(SignatureState.NULL_PARAMS_DATA);
        }
        return SignaturePattern.valueOf(signaturePattern);
    }

    private void handlerException(HttpServletResponse response, SignatureException signatureException) {
        SecurityContextHolder.clearContext();
        response.setStatus(signatureException.getValue());
        JSONUtil.writeValue(response, DataResult.fail(signatureException.getValue(), signatureException.getReasonPhrase()));
    }

    /**
     * 执行策略
     *
     * @param request request
     */
    private void executeStrategy(HttpServletRequest request) {
        final SignaturePattern pattern = this.extractSignaturePattern(request);
        SignatureAuthenticationStrategy strategy = this.strategies.get(pattern);
        if (strategy.supports(pattern)) {
            strategy.onAuthenticate(request);
        }
    }

}
