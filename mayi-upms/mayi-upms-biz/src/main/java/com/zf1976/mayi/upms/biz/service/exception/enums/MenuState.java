package com.zf1976.mayi.upms.biz.service.exception.enums;

/**
 * @author mac
 * @date 2020/12/17
 **/
public enum MenuState {

    /**
     * 数据不存在
     */
    MENU_NOT_FOUND(400, "菜单不存在"),

    /**
     * 数据已存在
     */
    MENU_EXISTING(400, "菜单：{}，已经存在"),

    /**
     * 信息已存在
     */
    MENU_INFO_EXISTING(400, "菜单信息：{}，已经存在"),

    /**
     * 操作异常
     */
    MENU_OPT_ERROR(400, "操作错误");

    /**
     * 状态值
     */
    private final int value;

    /**
     * 缘由
     */
    private final String reasonPhrase;

    MenuState(int value, String reasonPhrase) {
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
