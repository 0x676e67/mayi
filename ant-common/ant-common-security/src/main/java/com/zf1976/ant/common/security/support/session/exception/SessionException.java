package com.zf1976.ant.common.security.support.session.exception;

/**
 * 会话异常
 *
 * @author mac
 * @date 2021/5/16
 */
public class SessionException extends RuntimeException {

    public SessionException(String reason) {
        super(reason);
    }
}
