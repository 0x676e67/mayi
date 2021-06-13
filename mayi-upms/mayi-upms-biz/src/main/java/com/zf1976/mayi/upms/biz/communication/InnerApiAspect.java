package com.zf1976.mayi.upms.biz.communication;

import com.zf1976.mayi.common.core.util.IpUtil;
import com.zf1976.mayi.common.core.util.RequestUtil;
import com.zf1976.mayi.common.core.constants.SecurityConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author mac
 * @date 2021/6/13
 */
@Aspect
@Component
public class InnerApiAspect {

    private final DiscoveryClient discoveryClient;

    public InnerApiAspect(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Inner注解方法内部鉴权
     *
     * @param proceedingJoinPoint 切点
     * @param inner 注解
     * @return {@link Object}
     * @throws Throwable 异常
     */
    @Around("@annotation(com.zf1976.mayi.upms.biz.communication.Inner) && @annotation(inner)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Inner inner) throws Throwable {
        if (inner != null) {
            // 判断是否内部实例
            List<String> clientServices = this.discoveryClient.getServices();
            HttpServletRequest request = RequestUtil.getRequest();
            String ipAddress = RequestUtil.getIpAddress();
            boolean isAuthentication = false;
            if (IpUtil.isInnerIp(ipAddress)) {
                isAuthentication = this.checkAuthentication(request);
            } else {
                for (String service : clientServices) {
                    ServiceInstance serviceInstance = discoveryClient.getInstances(service).get(0);
                    if (serviceInstance != null &&serviceInstance.getHost().equals(ipAddress)) {
                        isAuthentication = this.checkAuthentication(request);
                        if (isAuthentication) {
                            return proceedingJoinPoint.proceed();
                        }
                    }
                }
            }
            if (!isAuthentication) {
                throw new InnerAuthenticationException("Internal service call authentication failed");
            }
        }
        return proceedingJoinPoint.proceed();
    }
    private boolean checkAuthentication(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.FROM);
        return ObjectUtils.nullSafeEquals(header, SecurityConstants.FROM_IN);
    }
}
