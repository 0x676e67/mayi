package com.zf1976.ant.upms.biz.exception.base;

/**
 * @author mac
 * @date 2020/12/17
 **/
public abstract class SysBaseException extends RuntimeException{

    protected  int value;

    protected  String reasonPhrase;

    protected String label;

    protected SysBaseException(int value, String reasonPhrase) {
        super(reasonPhrase);
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getLabel() {
        return this.label;
    }
}
