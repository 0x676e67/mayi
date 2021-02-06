package com.zf1976.ant.upms.biz.exp.enums;

/**
 * @author mac
 * @date 2020/12/17
 **/
public enum DictState {

    /**
     * 数据不存在
     */
    DICT_NOT_FOUND(400, "字典不存在"),

    /**
     * 数据已存在
     */
    DICT_EXISTING(400, "字典：{}，已经存在"),

    /**
     * 操作异常
     */
    DICT_OPT_ERROR(400, "操作错误");

    /**
     * 状态值
     */
    private final int value;

    /**
     * 缘由
     */
    private final String reasonPhrase;

    DictState(int value, String reasonPhrase) {
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
