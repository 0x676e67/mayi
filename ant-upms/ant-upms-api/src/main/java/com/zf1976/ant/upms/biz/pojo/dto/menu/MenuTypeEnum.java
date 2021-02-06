package com.zf1976.ant.upms.biz.pojo.dto.menu;

/**
 * 菜单类型枚举
 *
 * @author Windows
 */
public enum MenuTypeEnum {

    // 目录
    CONTENTS(0),

    // 菜单
    MENU(1),

    // 按钮
    BUTTON(2);

    public int type;

    MenuTypeEnum(int type) {
        this.type = type;
    }

    public static MenuTypeEnum match(int value) {
        for (MenuTypeEnum typeEnum : MenuTypeEnum.values()) {
            if (typeEnum.type == value) {
                return typeEnum;
            }
        }
        return null;
    }
}
