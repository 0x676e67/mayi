package com.zf1976.mayi.upms.biz.service.exception.enums;

/**
 * @author mac
 * @date 2020/12/17
 **/

public enum DepartmentState {

    /**
     * 数据不存在
     */
    DEPARTMENT_NOT_FOUND(400, "部门不存在"),

    /**
     * 数据已存在
     */
    DEPARTMENT_EXISTING(400, "部门：{}，已存在"),

    /**
     * 上级部门关闭状态
     */
    DEPARTMENT_PARENT_CLOSE(400, "上级部门处于关闭状态，禁止操作"),

    /**
     * pid不能设置为下级部门id
     */
    DEPARTMENT_BAN_PARENT(400, "禁止设置上级部门为下级子部门"),

    /**
     * pid不能设置为id
     */
    DEPARTMENT_BAN_CURRENT(400, "禁止设置上级部门为本级部门"),

    /**
     * 部门存在依赖关系
     */
    DEPARTMENT_DEPENDS_ERROR(400, "部门存在用户角色依赖，不能删除"),

    /**
     * 操作异常
     */
    DEPARTMENT_OPT_ERROR(400, "操作错误");

    /**
     * 状态值
     */
    private final int value;

    /**
     * 缘由
     */
    private final String reasonPhrase;

    DepartmentState(int value, String reasonPhrase) {
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
