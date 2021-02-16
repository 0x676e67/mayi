package com.zf1976.ant.auth.safe.filter.manager;

import com.zf1976.ant.auth.cache.session.Session;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.core.util.SpringContextHolder;
import com.zf1976.ant.auth.LoginUserDetails;
import com.zf1976.ant.auth.cache.session.repository.SessionRepository;
import com.zf1976.ant.auth.cache.session.service.SessionService;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 身份验证会话管理器
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
public class SessionContextHolder {

    private static final SessionService SESSION_SERVICE;
    private static final SessionRepository SESSION_REPOSITORY;

    static {
        SESSION_SERVICE = SpringContextHolder.getBean(SessionService.class);
        SESSION_REPOSITORY = SpringContextHolder.getBean(SessionRepository.class);
    }

    /**
     * 保存会话
     *
     * @param token              token
     * @param userDetails        用户会话details
     * @param httpServletRequest 请求
     */
    public static void set(String token, UserDetails userDetails, HttpServletRequest httpServletRequest) {
        SESSION_SERVICE.save(token, (LoginUserDetails) userDetails, httpServletRequest);
    }

    /**
     * 查询会话
     *
     * @param id token
     * @return session
     */
    public static Session get(Long id) {
        return SESSION_SERVICE.get(id);
    }

    /**
     * 获取会话
     *
     * @param token token
     * @return session
     */
    public static Session get(String token) {
        return SESSION_SERVICE.get(token);
    }

    /**
     * 更新session
     * token续期专用/用户信息修改时更新
     * @param id id
     * @param session session
     */
    public static void refresh(Long id, Session session) {
        HttpServletRequest request = RequestUtils.getRequest();
        session.setIp(RequestUtils.getIpAddress(request))
               .setIpRegion(RequestUtils.getIpRegion(request))
               .setBrowser(RequestUtils.getBrowser(request))
               .setOperatingSystemType(RequestUtils.getOpsSystemType(request));
        SESSION_SERVICE.update(id, session);
    }

    /**
     * 更新session
     * token续期专用/用户信息修改时更新
     * @param id id
     * @param session session
     * @param expired expiry time
     */
    public static void refresh(Long id, Session session, Long expired) {
        HttpServletRequest request = RequestUtils.getRequest();
        session.setIp(RequestUtils.getIpAddress(request))
               .setIpRegion(RequestUtils.getIpRegion(request))
               .setBrowser(RequestUtils.getBrowser(request))
               .setOperatingSystemType(RequestUtils.getOpsSystemType(request));
        SESSION_SERVICE.update(id, session, expired);
    }

    /**
     * 权限修改时更新会话
     *
     * @param token              token
     * @param userDetails        userDetails
     * @param httpServletRequest request
     */
    public static void refresh(String token, LoginUserDetails userDetails, HttpServletRequest httpServletRequest) {
        SESSION_SERVICE.update(token, userDetails, httpServletRequest);
    }

    /**
     * 强制下线
     *
     * @param token token
     */
    public static void removeSession(String token) {
        Optional.ofNullable(token)
                .ifPresent(SESSION_SERVICE::remove);
    }

    /**
     * 强制下线
     *
     * @param id id
     */
    public static void removeSession(Long id) {
        SESSION_SERVICE.remove(id);
    }

    /**
     * 查询session过期时间
     *
     * @param id token
     * @return timestamp
     */
    public static Long getExpired(Long id) {
        return SESSION_REPOSITORY.selectSessionExpiredById(id);
    }

}
