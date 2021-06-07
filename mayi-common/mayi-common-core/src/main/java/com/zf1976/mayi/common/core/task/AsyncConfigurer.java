package com.zf1976.mayi.common.core.task;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

import java.util.concurrent.Executor;

/**
 * 异步调用线程池配置
 *
 * @author mac
 * @date 2021/5/22
 */
public class AsyncConfigurer extends AsyncConfigurerSupport {

    public AsyncConfigurer() {
        super();
    }

    @Override
    public Executor getAsyncExecutor() {
        return super.getAsyncExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return super.getAsyncUncaughtExceptionHandler();
    }
}
