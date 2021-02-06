package com.zf1976.ant.upms.biz.exp;

import com.zf1976.ant.upms.biz.exp.base.SysBaseException;
import com.zf1976.ant.upms.biz.exp.enums.DictState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class DictException extends SysBaseException {

    public DictException(DictState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public DictException(DictState state, String label) {
        this(state);
        super.label = label;
    }


}
