package com.zf1976.ant.common.core.foundation.exception;

/**
 * @author mac
 */

public enum BusinessMsgState {

    /**
     * 空密码异常
     */
    NULL_PASSWORD(400, "password is not null!"),

    /**
     * 密码不合格异常
     */
    PASSWORD_LOW(400, "password is too simple!"),

    /**
     * 密码重复
     */
    PASSWORD_REPEAT(400, "password is not repeat!"),

    PASSWORD_NOT_MATCHING(400, "password is not matching raw password"),

    /**
     * 邮箱不合格
     */
    EMAIL_LOW(400, "email is too simple!"),

    /**
     * 用户名过于简单
     */
    USERNAME_LOW(400, "username is too simple!"),

    /**
     * 非手机号码
     */
    NOT_PHONE(400, "this is not phone!"),

    /**
     * 邮箱已存在
     */
    EMAIL_EXISTING(400, "email is existing!"),

    /**
     * 手机号已存在
     */
    PHONE_EXISTING(400, "phone is existing!"),

    /**
     * 操作异常
     */
    OPT_ERROR(400, "operations error!"),

    /**
     * 验证码未找到
     */
    CODE_NOT_FOUNT(400, "validate code is not found!"),

    /**
     * 参数异常
     */
    PARAM_ILLEGAL(400, "illegal parameters!"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUNT(400, "data is not found!"),

    /**
     * 数据已存在
     */
    DATA_EXISTING(400, "data is existing!"),

    /**
     * 版本已更新
     */
    VERSION_IS_UPDATE(400, "version is update!"),

    /**
     * 上传失败
     */
    UPLOAD_ERROR(400, "upload error!"),

    /**
     * 下载失败
     */
    DOWNLOAD_ERROR(400,"download error!"),

    /**
     * ID 为空
     */
    ID_NULL(400,"id is null");

    private final int value;

    private final String reasonPhrase;

    BusinessMsgState(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
