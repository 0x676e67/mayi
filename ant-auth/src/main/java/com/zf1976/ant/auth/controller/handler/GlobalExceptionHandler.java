package com.zf1976.ant.auth.controller.handler;

import com.zf1976.ant.auth.exception.SecurityException;
import com.zf1976.ant.common.core.foundation.DataResult;
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
     * @date 2021-05-12 08:53:14
     * @param securityException
     * @return {@link DataResult}
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult handleSecurityException(SecurityException securityException) {
        return DataResult.fail(securityException.getValue(), securityException.getMessage());
    }


}
