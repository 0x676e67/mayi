package com.zf1976.mayi.auth.exception;

import com.zf1976.mayi.common.security.enums.AuthenticationState;
import org.springframework.security.core.AuthenticationException;

/**
 * @author mac
 */
public class PasswordException extends AuthenticationException {

    private final int value;

    private final String reasonPhrase;


    public PasswordException(AuthenticationState e) {
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
