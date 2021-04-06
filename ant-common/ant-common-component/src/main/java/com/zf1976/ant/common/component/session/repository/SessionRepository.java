package com.zf1976.ant.common.component.session.repository;

import com.zf1976.ant.common.component.session.Session;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 二级session 缓存 repository
 *
 * @author mac
 * Create by Ant on 2020/9/28 23:30
 */
public interface SessionRepository {

    /**
     * id 指向session 保存会话信息
     *
     * @param id   令牌
     * @param session vo
     */
    void saveSessionById(Long id, Session session);

    /**
     * 用户token 指向 id
     *
     * @param token token
     * @param id  id
     */
    void savaIdByToken(String token, Long id);

    /**
     * 更新会话
     *
     * @param id id
     * @param session session
     * @param expired expired timestamp
     */
    void updateSessionById(Long id, Session session, long expired);

    /**
     * 更新会话
     *
     * @param token token
     * @param session session
     * @param expired expired timestamp
     */
    void updateSessionByToken(String token, Session session, long expired);

    /**
     * 查询token
     *
     * @param token token
     * @return id
     */
    Long selectIdByToken(String token);


    /**
     * 根据id查询会话信息
     *
     * @param id id
     * @return session session
     */
    Session selectSessionById(Long id);

    /**
     * 根据会话id集合 获取会话集合
     *
     * @param ids id集合
     * @return /
     */
    List<Session> selectSessionByIds(Collection<Long> ids);

    /**
     * delete
     *
     * @param token 用户token
     */
    void deleteIdByToken(String token);

    /**
     * delete
     *
     * @param id id
     */
    void deleteSessionById(Long id);

    /**
     * 查询健过期时间
     *
     * @param id key
     * @return long
     */
    Long selectSessionExpiredById(Long id);

    /**
     * 查询session过期时间
     *
     * @param id key
     * @param timeUnit 时间单位
     * @return long
     */
    Long selectSessionExpiredById(Long id, TimeUnit timeUnit);

    /**
     * 是否存在会话
     *
     * @param id id
     * @return boolean
     */
    Boolean hasSession(Long id);
}
