package com.zf1976.ant.common.security;

import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.security.cache.session.Session;
import com.zf1976.ant.common.security.cache.session.service.SessionService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * 身份验证会话管理器
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public class SessionContextHolder {

    private static SessionService service;

    public SessionContextHolder(SessionService sessionService) {
        SessionContextHolder.service = sessionService;
    }

    /**
     * 存储会话
     *
     * @param token              token
     * @param userDetails        用户会话details
     */
    public static void storeSession(String token, UserDetails userDetails) {
        service.save(token, (AntUserDetails) userDetails);
    }

    /**
     * 读取会话
     *
     * @param id token
     * @return session
     */
    public static Session readSession(Long id) {
        return service.get(id);
    }

    /**
     * 获取会话
     *
     * @param token token
     * @return session
     */
    public static Session readSession(String token) {
        return service.get(token);
    }

    /**
     * 更新session
     * token续期专用/用户信息修改时更新
     * @param id id
     * @param session session
     */
    public static void refreshSession(Long id, Session session) {
        HttpServletRequest request = RequestUtils.getRequest();
        session.setIp(RequestUtils.getIpAddress(request))
               .setIpRegion(RequestUtils.getIpRegion(request))
               .setBrowser(RequestUtils.getBrowser(request))
               .setOperatingSystemType(RequestUtils.getOpsSystemType(request));
        service.update(id, session);
    }

    /**
     * 更新session
     * token续期专用/用户信息修改时更新
     * @param id id
     * @param session session
     * @param expired expiry time
     */
    public static void refreshSession(Long id, Session session, Long expired) {
        HttpServletRequest request = RequestUtils.getRequest();
        session.setIp(RequestUtils.getIpAddress(request))
               .setIpRegion(RequestUtils.getIpRegion(request))
               .setBrowser(RequestUtils.getBrowser(request))
               .setOperatingSystemType(RequestUtils.getOpsSystemType(request));
        service.update(id, session, expired);
    }

    /**
     * 权限修改时更新会话
     *
     * @param token              token
     * @param userDetails        userDetails
     */
    public static void refreshSession(String token, AntUserDetails userDetails) {
        service.update(token, userDetails);
    }

    /**
     * 强制下线
     *
     * @param token token
     */
    public static void removeSession(String token) {
        Optional.ofNullable(token).ifPresent(service::remove);
    }

    /**
     * 强制下线
     *
     * @param id id
     */
    public static void removeSession(Long id) {
        service.remove(id);
    }


    /**
     * 查询session过期时间
     *
     * @param id token
     * @return timestamp
     */
    public static Long getExpiredTime(Long id) {
        return service.getExpired(id);
    }

    /**
     * 获取当前会话id
     *
     * @return session
     */
    public static Long getSessionId(){
        return service.getSessionId(token());
    }

    /**
     * 获取当前会话数据权限
     *
     * @return data permission
     */
    public static List<Long> getDataPermission(){
        return service.get(token())
                      .getDataPermission();
    }

    private static String token(){
        return RequestUtils.getRequest()
                           .getHeader(HttpHeaders.AUTHORIZATION);
    }

}
