package com.zf1976.ant.auth.safe.filter.signature.impl;

import com.zf1976.ant.auth.enums.SignatureState;
import com.zf1976.ant.auth.exception.SignatureException;
import com.zf1976.ant.auth.safe.filter.signature.AbstractSignatureAuthenticationStrategy;
import com.zf1976.ant.auth.safe.filter.signature.SignaturePattern;
import com.zf1976.ant.auth.safe.filter.signature.StandardSignature;
import com.zf1976.ant.common.core.util.CaffeineCacheUtils;
import org.springframework.util.NumberUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 开放签名认证策略
 *
 * @author mac
 * @date 2021/1/29
 **/
public class OpenSignatureAuthenticationStrategy extends AbstractSignatureAuthenticationStrategy {

    public OpenSignatureAuthenticationStrategy() {
        super();
    }

    @Override
    public void onAuthenticate(HttpServletRequest request) {
        // 验证是否缺少参数
        super.validateParameters(request);
        // 获取原签名
        String rawSignature = super.getAndValidateSignature(request);
        // 获取时间戳
        Long timestamp;
        try {
            String timestampStr = request.getParameter(StandardSignature.TIMESTAMP);
            timestamp = NumberUtils.parseNumber(timestampStr, Long.class);
        } catch (Exception e) {
            throw new SignatureException(SignatureState.MISSING_TIMESTAMP, e.getMessage());
        }
        // 获取随机字符串
        String nonceStr = request.getParameter(StandardSignature.NONCE_STR);
        // 防重放校验
        super.validatePreventReplyAttack(nonceStr, timestamp);
        // 验证应用标识
        String appId = super.getAndValidateAppId(request);
        // 构建签名
        String generateSignature = super.generateSignature(appId, nonceStr, timestamp);
        // 校验签名是否相同
        super.validateSignature(rawSignature, generateSignature);
        // 签名校验成功，缓存签名防止重放
        CaffeineCacheUtils.setFixedOneMinutes(nonceStr, rawSignature);
    }

    @Override
    public boolean supports(SignaturePattern signatureModel) {
        return signatureModel != null;
    }


}
