package com.zf1976.ant.upms.biz.exp.enums;

/**
 * @author mac
 * @date 2020/12/17
 **/
public enum RoleState {

    /**
     * 数据不存在
     */
    ROLE_NOT_FOUND(400, "角色不存在"),

    /**
     * 数据已存在
     */
    ROLE_EXISTING(400, "角色：{}，已经存在"),

    /**
     * 存在依赖
     */
    ROLE_DEPENDS_ERROR(400, "角色存在用户依赖，不能删除"),

    /**
     * 操作异常
     */
    ROLE_OPT_ERROR(400, "操作错误");

    /**
     * 状态值
     */
    private final int value;

    /**
     * 缘由
     */
    private final String reasonPhrase;

    RoleState(int value, String reasonPhrase) {
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
