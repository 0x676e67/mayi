package com.zf1976.ant.auth.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * token返回增强切面
 *
 * @author mac
 * @date 2021/2/16
 **/
@Aspect
@Component
public class AuthorizationTokenAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Before("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public void before(JoinPoint joinPoint) {

    }

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object tokenEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // 放行
        return joinPoint.proceed();
    }
}
