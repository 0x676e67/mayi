package com.zf1976.ant.upms.biz.service.exception;

import com.zf1976.ant.upms.biz.service.exception.enums.PositionState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class PositionException extends SysBaseException {

    public PositionException(PositionState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public PositionException(PositionState state, String label) {
        this(state);
        super.label = label;
    }

}
