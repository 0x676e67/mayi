package com.zf1976.mayi.upms.biz.service.exception;

import com.zf1976.mayi.upms.biz.service.exception.enums.DepartmentState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class DepartmentException extends SysBaseException {

    public DepartmentException(DepartmentState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public DepartmentException(DepartmentState state,String label) {
        this(state);
        super.label = label;
    }
}
