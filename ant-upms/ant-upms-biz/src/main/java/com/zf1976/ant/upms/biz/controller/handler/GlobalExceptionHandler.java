package com.zf1976.ant.upms.biz.controller.handler;

import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.security.support.session.exception.SessionException;
import com.zf1976.ant.upms.biz.exception.base.SysBaseException;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author ant
 */
@RestControllerAdvice
@SuppressWarnings("rawtypes")
public class GlobalExceptionHandler {


    /**
     * 全局异常类（拦截不到子类型处理）
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    DataResult exceptionHandler(Exception exception) {
        return DataResult.fail("Server Error");
    }

    /**
     * 业务异常类
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult badBusinessExceptionHandler(BusinessException exception) {
        return DataResult.fail(exception.getValue(), exception.getReasonPhrase());
    }

    /**
     * 方法参数异常
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult validateExceptionHandler(MethodArgumentNotValidException exception) {
        String messages = exception.getBindingResult()
                                   .getAllErrors()
                                   .stream()
                                   .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                   .collect(Collectors.joining(","));
        return DataResult.fail(messages);
    }

    /**
     * 后台系统业务异常
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(SysBaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult SysBaseExceptionHandler(SysBaseException exception) {
        String message;
        if (exception.getLabel() != null) {
            message = MessageFormatter.format(exception.getReasonPhrase(), exception.getLabel())
                                      .getMessage();
        } else {
            message = exception.getReasonPhrase();
        }
        return DataResult.fail(exception.getValue(), message);
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
