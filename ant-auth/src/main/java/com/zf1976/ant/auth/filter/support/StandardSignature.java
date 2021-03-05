package com.zf1976.ant.auth.filter.support;

/**
 * @author mac
 * @date 2021/1/29
 **/
public final class StandardSignature {
    /**
     * 应用唯一标识
     */
    public static final String APPLY_ID = "apply_id";

    /**
     * 应用密钥
     */
    public static final String APPLY_SECRET = "apply_secret";

    /**
     * 时间戳
     */
    public static final String TIMESTAMP = "timestamp";

    /**
     * 随机字符串
     */
    public static final String NONCE_STR = "nonce_str";

    /**
     * 签名
     */
    public static final String SIGNATURE = "sign";

    /**
     * 参数加密传送content
     */
    public static final String SECRET_CONTENT = "Content";

    /**
     * 签名模式
     */
    public static final String SIGN_PATTERN = "pattern";
}

