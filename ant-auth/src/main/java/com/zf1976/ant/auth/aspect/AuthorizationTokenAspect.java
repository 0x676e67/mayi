package com.zf1976.ant.auth.aspect;

import com.fasterxml.jackson.core.JsonFactory;
import com.power.common.util.JsonFormatUtil;
import com.power.common.util.StringUtil;
import com.zf1976.ant.common.core.foundation.Result;
import okhttp3.internal.http2.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.util.Map;

/**
 * token返回增强切面
 *
 * @author mac
 * @date 2021/2/16
 **/
@Aspect
@Component
@SuppressWarnings("unchecked")
public class AuthorizationTokenAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object tokenEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // 放行
        Object proceed = joinPoint.proceed();
        ResponseEntity<OAuth2AccessToken> responseEntity = ((ResponseEntity<OAuth2AccessToken>) proceed);
        OAuth2AccessToken oAuth2AccessToken;
        Result<OAuth2AccessToken> result;
        if (proceed != null) {
            oAuth2AccessToken = responseEntity.getBody();
            if (oAuth2AccessToken != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("post access token:{}", oAuth2AccessToken.getValue());
                }
                result = Result.success(oAuth2AccessToken);
            } else {
                logger.error("error:{}", responseEntity.getStatusCode().toString());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED);
            }
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok();
    }
}
