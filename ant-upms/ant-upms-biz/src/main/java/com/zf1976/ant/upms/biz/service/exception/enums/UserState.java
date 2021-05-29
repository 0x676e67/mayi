package com.zf1976.ant.upms.biz.service.exception.enums;

/**
 * @author mac
 * 2020/12/17
 */
public enum UserState {

    /**
     * 数据不存在
     */
    USER_NOT_FOUND(400, "用户不存在"),

    /**
     * 数据已存在
     */
    USER_EXISTING(400, "用户：{}，已存在"),

    /**
     * 用户信息已存在
     */
    USER_INFO_EXISTING(400, "用户信息：{}，已存在"),

    /**
     * 操作异常
     */
    USER_OPT_ERROR(400, "操作错误"),

    USER_OPT_DISABLE_ONESELF_ERROR(400, "Prohibit disabling the current operating user");

    /**
     * 状态值
     */
    private final int value;

    /**
     * 缘由
     */
    private final String reasonPhrase;

    UserState(int value, String reasonPhrase) {
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
