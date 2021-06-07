package com.zf1976.mayi.auth.controller.handler;

import com.zf1976.mayi.auth.exception.SecurityException;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.security.support.session.exception.SessionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author mac
 * @date 2021/5/7
 */
@SuppressWarnings("ALL")
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 安全管理异常拦截处理
     *
     * @param exception
     * @return {@link DataResult}
     * @date 2021-05-12 08:53:14
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult handleSecurityException(SecurityException exception) {
        return DataResult.fail(exception.getValue(), exception.getMessage());
    }

    /**
     * 会话状态异常
     *
     * @param exception 会话异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(SessionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    DataResult handleSessionException(SessionException exception) {
        return DataResult.fail(exception);
    }


}
