package com.zf1976.ant.upms.biz.exp.enums;

/**
 * @author mac
 * @date 2020/12/17
 **/
public enum JobState {

    /**
     * 数据不存在
     */
    JOB_NOT_FOUND(400, "岗位不存在"),

    /**
     * 数据已存在
     */
    JOB_EXISTING(400, "岗位：{}，已经存在"),

    /**
     * 操作异常
     */
    JOB_OPT_ERROR(400, "操作错误");

    /**
     * 状态值
     */
    private final int value;

    /**
     * 缘由
     */
    private final String reasonPhrase;

    JobState(int value, String reasonPhrase) {
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
