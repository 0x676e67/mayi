package com.zf1976.ant.upms.biz.service.exception;

import com.zf1976.ant.upms.biz.service.exception.enums.UserState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class UserException extends SysBaseException {

    public UserException(UserState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public UserException(UserState state, String label) {
        this(state);
        super.label = label;
    }
}
