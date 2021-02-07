package com.zf1976.ant.upms.biz.exception;

import com.zf1976.ant.upms.biz.exception.base.SysBaseException;
import com.zf1976.ant.upms.biz.exception.enums.JobState;

/**
 * @author mac
 * @date 2020/12/17
 **/
public class JobException extends SysBaseException {

    public JobException(JobState state) {
        super(state.getValue(), state.getReasonPhrase());
    }

    public JobException(JobState state, String label) {
        this(state);
        super.label = label;
    }

}
