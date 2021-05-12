package com.zf1976.ant.upms.biz.controller.handler;

import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.upms.biz.exception.base.SysBaseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author ant
 */
@Slf4j
@RestControllerAdvice
@SuppressWarnings("rawtypes")
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    DataResult exceptionHandler(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()), e);
        return DataResult.fail("internal server error");
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult badBusinessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(), e.getCause());
        return DataResult.fail(e.getValue(), e.getReasonPhrase());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult validateExceptionHandler(MethodArgumentNotValidException e) {
        String messages = e.getBindingResult()
                           .getAllErrors()
                           .stream()
                           .map(DefaultMessageSourceResolvable::getDefaultMessage)
                           .collect(Collectors.joining(","));
        log.error(messages, e.getCause());
        return DataResult.fail(messages);
    }

    @ExceptionHandler(SysBaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult departmentExceptionHandler(SysBaseException e) {
        String message;
        if (e.getLabel() != null) {
            message = MessageFormatter.format(e.getReasonPhrase(), e.getLabel())
                                      .getMessage();
        } else {
            message = e.getReasonPhrase();
        }
        log.error(message, e.getCause());
        return DataResult.fail(e.getValue(), message);
    }
}
