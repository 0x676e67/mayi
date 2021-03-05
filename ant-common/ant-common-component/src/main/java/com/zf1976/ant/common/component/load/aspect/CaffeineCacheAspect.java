package com.zf1976.ant.common.component.load.aspect;


import com.power.common.util.StringUtil;
import com.zf1976.ant.common.component.load.CaffeineCacheProvider;
import com.zf1976.ant.common.component.load.annotation.CaffeineEvict;
import com.zf1976.ant.common.component.load.annotation.CaffeinePut;
import com.zf1976.ant.common.component.load.aspect.handler.SpringElExpressionHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.SmartLifecycle;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author WINDOWS
 */
@Aspect
@Slf4j(topic = "[caffeine]-[cache]")
@Component
public class CaffeineCacheAspect {

    private final SpringElExpressionHandler handler;
    private final CaffeineCacheProvider<Object, Object> caffeineCacheProvider;
    public CaffeineCacheAspect() {
        this.caffeineCacheProvider = new CaffeineCacheProvider<>();
        this.handler = new SpringElExpressionHandler();
        this.checkStatus();
    }
    private void checkStatus() {
        Assert.notNull(this.handler, "Uninitialized!");
        Assert.notNull(this.caffeineCacheProvider, "Uninitialized!");
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CaffeinePut) && @annotation(annotation))")
    public Object saveCache(ProceedingJoinPoint joinPoint, CaffeinePut annotation) {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String  key = this.handler.parse(method, joinPointArgs, annotation.key(), String.class, annotation.key());
        if (annotation.dynamicsKey()) {
            key = this.formatKey(this.getPrincipal(), key);
        }
        return this.caffeineCacheProvider.get(annotation.namespace(), key, annotation.expired(), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                if (log.isWarnEnabled()) {
                    log.warn(throwable.getMessage(), throwable);
                }
                return Optional.empty();
            }
        });
    }

    @Around("@annotation(com.zf1976.ant.common.component.load.annotation.CaffeineEvict) && @annotation(annotation)")
    public Object removeCache(ProceedingJoinPoint joinPoint, CaffeineEvict annotation) throws Throwable {
        Method method = this.handler.filterMethod(joinPoint);
        Object[] joinPointArgs = joinPoint.getArgs();
        String namespace = annotation.namespace();
        String[] dependOnNamespace = annotation.dependsOn();
        if (StringUtil.isEmpty(annotation.key())) {
            Arrays.stream(dependOnNamespace)
                  .forEachOrdered(this.caffeineCacheProvider::invalidate);
            this.caffeineCacheProvider.invalidate(namespace);
        } else {
            String key = this.handler.parse(method, joinPointArgs, annotation.key(), String.class, null);
            this.caffeineCacheProvider.invalidate(namespace, key);
        }
        return joinPoint.proceed();
    }

    private String formatKey(Object prefix, String key) {
        return prefix + "-" + key;
    }


    private String getPrincipal() {
        return (String) SecurityContextHolder.getContext()
                                             .getAuthentication()
                                             .getPrincipal();
    }

}
