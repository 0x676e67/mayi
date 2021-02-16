package com.zf1976.ant.auth.filter.signature;

import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.dev.SecurityProperties;
import com.zf1976.ant.common.core.util.ApplicationConfigUtils;
import com.zf1976.ant.common.core.util.CaffeineCacheUtils;
import com.zf1976.ant.common.encrypt.EncryptUtil;
import com.zf1976.ant.auth.exception.SignatureException;
import com.zf1976.ant.auth.enums.SignatureState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author mac
 * @date 2021/1/30
 **/
public abstract class AbstractSignatureAuthenticationStrategy implements SignatureAuthenticationStrategy{

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractSignatureAuthenticationStrategy.class);
    protected Map<String, SignatureState> stateMap = new HashMap<>(4);
    protected Long preventReplyAttackTime = 60000L;

    protected AbstractSignatureAuthenticationStrategy() {
        stateMap.put(StandardSignature.APP_ID, SignatureState.MISSING_APP_ID);
        stateMap.put(StandardSignature.TIMESTAMP, SignatureState.MISSING_TIMESTAMP);
        stateMap.put(StandardSignature.NONCE_STR, SignatureState.MISSING_NONCE_STR);
        stateMap.put(StandardSignature.SIGNATURE, SignatureState.MISSING_SIGN);
    }

    /**
     * 校验签名
     *
     * @param rawSignature 原签名
     * @param generateSignature 构建签名
     */
    protected void validateSignature(String rawSignature, String generateSignature) {
        if (!ObjectUtils.nullSafeEquals(generateSignature, rawSignature)) {
            throw new SignatureException(SignatureState.SIGNATURE_MISMATCH);
        }
    }

    protected String getAndValidateSignature(HttpServletRequest request) {
        String sign = request.getParameter(StandardSignature.SIGNATURE);
        if (StringUtil.isEmpty(sign)) {
            throw new SignatureException(SignatureState.MISSING_SIGN);
        }
        return sign;
    }

    protected String getAndValidateAppId(HttpServletRequest request) {
        String appKey = request.getParameter(StandardSignature.APP_ID);
        this.validateAppId(appKey, this.getAppId());
        return appKey;
    }

    protected String getAndValidateSignature(Map<String, String> kv) {
        String sign = kv.get(StandardSignature.SIGNATURE);
        if (StringUtil.isEmpty(sign)) {
            throw new SignatureException(SignatureState.MISSING_SIGN);
        }
        return sign;
    }

    protected String getAndValidateAppId(Map<String, String> kv) {
        String appKey = kv.get(StandardSignature.APP_ID);
        this.validateAppId(appKey, this.getAppId());
        return appKey;
    }

    private void validateAppId(String key, String realKey) {
        if (!ObjectUtils.nullSafeEquals(key, realKey)) {
            throw new SignatureException(SignatureState.MISSING_APP_ID);
        }
    }


    /**
     * 校验参数
     *
     * @param content content
     */
    protected void validateParameters(Map<String, String> content) {
        Assert.notNull(content,"content parameter cannot been null");
        for (Map.Entry<String, SignatureState> stateEntry : stateMap.entrySet()) {
            String parameter = content.get(stateEntry.getKey());
            if (StringUtil.isEmpty(parameter)) {
                throw new SignatureException(stateEntry.getValue());
            }
        }
    }

    /**
     * 验证参数是否缺少参数
     *
     * @param request request
     */
    protected void validateParameters(HttpServletRequest request) {
        Assert.notNull(request, "request cannot been null");
        request.getParameterMap()
               .forEach((s, strings) -> {
                   System.out.println(s);
                   System.out.println(Arrays.toString(strings));
               });
        for (Map.Entry<String, SignatureState> stateEntry : stateMap.entrySet()) {
            String parameter = request.getParameter(stateEntry.getKey());
            if (StringUtil.isEmpty(parameter)) {
                throw new SignatureException(stateEntry.getValue());
            }
        }
    }

    /**
     * 时间戳防重放校验
     *
     * @param timestamp 时间戳
     */
    protected void validatePreventReplyAttack(Long timestamp) {
        if (System.currentTimeMillis() - timestamp > preventReplyAttackTime) {
            throw new SignatureException(SignatureState.ERROR_REPLY_ATTACK);
        }
    }

    /**
     * 随机字符串防重放校验
     *
     * @param nonceStr 随机字符串
     */
    protected void validatePreventReplyAttack(String nonceStr) {
        Object rawSignature = CaffeineCacheUtils.getFixedOneMinutes(nonceStr);
        if (rawSignature != null) {
            throw new SignatureException(SignatureState.ERROR_REPLY_ATTACK);
        }
    }

    /**
     * 防重放校验
     *
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     */
    protected void validatePreventReplyAttack(String nonceStr, Long timestamp) {
        this.validatePreventReplyAttack(timestamp);
        this.validatePreventReplyAttack(nonceStr);
    }



    /**
     * 构建签名
     *
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     * @return /
     */
    protected String generateSignature(String appId,String nonceStr, Long timestamp) {
        List<String> params = new ArrayList<>();
        params.add(StandardSignature.APP_ID + "=" + appId);
        params.add(StandardSignature.APP_KEY + "=" + this.getAppKey());
        params.add(StandardSignature.NONCE_STR + "=" + nonceStr);
        params.add(StandardSignature.TIMESTAMP + "=" + timestamp.toString());
        params.sort(String::compareTo);
        String signatureParams = String.join("&", params);
        return EncryptUtil.signatureByHmacSha1(signatureParams);
    }

    /**
     * 获取应用唯一标识
     *
     * @return /
     */
    protected String getAppId() {
        return this.getSecurityProperties()
                   .getAppId();
    }

    /**
     * 获取应用密钥
     *
     * @return /
     */
    private String getAppKey() {
        return this.getSecurityProperties()
                   .getAppKey();
    }

    /**
     * 获取安全配置
     *
     * @return /
     */
    private SecurityProperties getSecurityProperties() {
        return ApplicationConfigUtils.getSecurityProperties();
    }

}
