package com.zf1976.ant.upms.biz.exception;

import com.zf1976.ant.upms.biz.exception.base.SysBaseException;
import com.zf1976.ant.upms.biz.exception.enums.RoleState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class RoleException extends SysBaseException {

    public RoleException(RoleState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public RoleException(RoleState state, String label) {
        this(state);
        super.label = label;
    }
}
