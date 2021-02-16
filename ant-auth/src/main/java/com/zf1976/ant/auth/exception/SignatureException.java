package com.zf1976.ant.auth.exception;

import com.zf1976.ant.auth.enums.SignatureState;

/**
 * @author mac
 * @date 2021/1/29
 **/
public class SignatureException extends RuntimeException {

    private final int value;
    private final String reasonPhrase;

    public SignatureException(SignatureState e) {
        super(e.getReasonPhrase());
        this.value = e.getValue();
        this.reasonPhrase = e.getReasonPhrase();
    }

    public SignatureException(SignatureState e, String reasonPhrase) {
        super(reasonPhrase);
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
