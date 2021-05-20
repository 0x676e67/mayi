package com.zf1976.ant.common.core.foundation.exception;


/**
 * 业务异常类
 *
 * @author mac
 */
public class BusinessException extends RuntimeException{

    private final int value;

    private final String reasonPhrase;

    public BusinessException(BusinessMsgState e) {
        super(e.getReasonPhrase());
        this.value = e.getValue();
        this.reasonPhrase = e.getReasonPhrase();
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
