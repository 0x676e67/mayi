package com.zf1976.ant.auth.cache.session.service;

import com.zf1976.ant.auth.AntUserDetails;
import com.zf1976.ant.auth.cache.session.Session;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mac
 * Create by Ant on 2020/9/29 19:14
 */
public interface SessionService {

    /**
     * 保存会话信息
     *
     * @param userDetails 详情
     * @param token       令牌
     * @param request     详情
     */
    void save(String token, AntUserDetails userDetails, HttpServletRequest request);

    /**
     * 更新会话
     *
     * @param token       令牌
     * @param userDetails 详情
     * @param request     详情
     */
    void update(String token, AntUserDetails userDetails, HttpServletRequest request);

    /**
     * 更新会话
     *
     * @param id id
     * @param session session
     */
    void update(Long id, Session session);

    /**
     * 更新会话
     *
     * @param id id
     * @param session session
     * @param expired  expired timestamp
     */
    void update(Long id, Session session, Long expired);

    /**
     * 强制下线
     *
     * @param userId 用户id
     */
    void remove(Long userId);

    /**
     * 强制下线
     *
     * @param token token
     */
    void remove(String token);

    /**
     * 查询会话
     *
     * @param id id
     * @return session
     */
    Session get(Long id);

    /**
     * 查询会话
     *
     * @param token token
     * @return session
     */
    Session get(String token);

}
