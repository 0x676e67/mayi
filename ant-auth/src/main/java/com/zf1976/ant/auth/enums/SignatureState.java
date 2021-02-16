package com.zf1976.ant.auth.enums;

/**
 * 接口签名state
 *
 * @author mac
 * @date 2021/1/29
 **/
public enum SignatureState {

    /**
     * 操作成功 {@code code=1}
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败，请稍候再试 {@code code=0}
     */
    FATAL(0, "操作失败，请稍候再试"),

    /**
     * 重放错误
     */
    ERROR_REPLY_ATTACK(403,"禁止重复请求"),

    /**
     * 参数解析失败 {@code code=403}
     */
    PARAMETER_ANALYSIS_FAILURE(403, "参数解析失败"),

    /**
     * 接口授权认证失败，签名不匹配 {@code code=403}
     */
    SIGNATURE_MISMATCH(403, "接口授权认证失败，签名不匹配"),

    /**
     * 参数不足 {@code code=403}
     */
    NULL_PARAMS_DATA(403, "参数不足"),

    /**
     * 错误参数
     */
    ERROR_PARAMS_DATA(403,"错误参数"),

    /**
     * 参数值为空 {@code code=403}
     */
    NULL_PARAMS_VALUE(403, "参数值为空"),

    /**
     * 缺少timestamp参数 {@code code=403}
     */
    MISSING_TIMESTAMP(403, "缺少timestamp参数"),

    /**
     * 缺少nonceStr参数 {@code code=403}
     */
    MISSING_NONCE_STR(403, "缺少nonceStr参数"),

    /**
     * 缺少appId参数 {@code code=403}
     */
    MISSING_APP_ID(403, "缺少appId参数"),

    /**
     * 缺少sign参数 {@code code=403}
     */
    MISSING_SIGN(403, "缺少sign参数"),

    /**
     * 服务器执行错误 {@code code=500}
     */
    ERROR(500, "服务器执行错误"),

    /**
     * 请求数据不存在，或数据集为空 {@code code=60000}
     */
    NULL_RESULT_DATA(60000, "请求数据不存在，或数据集为空");

    private final int value;

    private final String reasonPhrase;

    SignatureState(int value, String text) {
        this.value = value;
        this.reasonPhrase = text;
    }

    /**
     * 获取value
     *
     * @return int
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取reasonPhrase
     *
     * @return String
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

}
