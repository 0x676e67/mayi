package com.zf1976.ant.upms.biz.service.exception;

import com.zf1976.ant.upms.biz.service.exception.enums.MenuState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class MenuException extends SysBaseException {

    public MenuException(MenuState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public MenuException(MenuState state, String label) {
        this(state);
        super.label = label;
    }
}
