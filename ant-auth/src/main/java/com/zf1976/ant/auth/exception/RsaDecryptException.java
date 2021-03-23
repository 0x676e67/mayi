package com.zf1976.ant.auth.exception;

import com.zf1976.ant.common.security.enums.AuthenticationState;
import org.springframework.security.core.AuthenticationException;

/**
 * @author mac
 */
public class RsaDecryptException extends AuthenticationException {

    private final int value;

    private final String reasonPhrase;


    public RsaDecryptException(AuthenticationState e) {
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
