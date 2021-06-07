package com.zf1976.mayi.upms.biz.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;


/**
 * @author ant
 * Create by Ant on 2020/9/15 8:29 下午
 */
public enum DataPermissionEnum implements IEnum<Integer> {

    /**
     * 用户部门权限
     */
    LEVEL(0, "用户"),
    /**
     * 自定义数据权限
     */
    CUSTOMIZE(1, "自定义"),
    /**
     * 全部的数据权限
     */
    ALL(2, "全部");

    @EnumValue
    public final int value;

    public final String description;

    DataPermissionEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
