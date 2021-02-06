package com.zf1976.ant.upms.biz.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author mac
 * @date 2021/1/1
 **/
public enum GenderEnum implements IEnum<Integer> {

    /**
     * 男性枚举
     */
    MAN(0,"男"),

    /**
     * 女性枚举
     */
    WOMAN(1,"女");

    @EnumValue
    private final int value;

    @JsonValue
    private final String sex;

    GenderEnum(int value, String sex) {
        this.value = value;
        this.sex = sex;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString(){
        return this.sex;
    }

}
