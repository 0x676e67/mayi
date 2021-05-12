package com.zf1976.ant.auth.exception;

/**
 * @author mac
 * @date 2021/5/12
 */
public class SecurityException extends RuntimeException {

    private int value = 400;

    private String message;

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public SecurityException(String message) {
        super(message);
        this.message = message;
    }

    public int getValue() {
        return value;
    }
}
