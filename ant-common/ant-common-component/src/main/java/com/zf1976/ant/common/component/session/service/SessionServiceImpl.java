package com.zf1976.ant.common.component.session.service;

import com.zf1976.ant.common.component.session.Session;
import com.zf1976.ant.common.component.session.repository.SessionRepository;
import com.zf1976.ant.common.security.property.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author mac
 * Create by Ant on 2020/9/29 19:16
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repository;
    private final SecurityProperties securityProperties;

    public SessionServiceImpl(SessionRepository repository,
                              SecurityProperties securityProperties) {
        this.repository = repository;
        this.securityProperties = securityProperties;
    }

    @Override
    public void save(String token, Session session) {
        try {
            var sessionId = session.getId();
            repository.savaIdByToken(token, sessionId);
            repository.saveSessionById(sessionId, session);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(String token, Session session) {
        try{
            this.remove(session.getId());
            this.save(token, session);
        }catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(Long id, Session session) {
        try {
            if (repository.hasSession(id)) {
                Long expired = repository.selectSessionExpiredById(id);
                if (expired > 0) {
                    repository.updateSessionById(id, session, expired);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(Long id, Session session, Long expired) {
        try {
            if (this.repository.hasSession(id)) {
                if (expired > 0) {
                    this.repository.updateSessionById(id, session, expired);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Session get(Long id) {
        try {
            return repository.selectSessionById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            return null;
        }
    }

    @Override
    public Session get(String token) {
        try {
            Long id = repository.selectIdByToken(token);
            return repository.selectSessionById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            return null;
        }
    }

    @Override
    public void remove(Long id) {
        try {
            Optional.ofNullable(repository.selectSessionById(id))
                    .ifPresent(session -> {
                        repository.deleteSessionById(id);
                        repository.deleteIdByToken(session.getToken());
                    });
        } catch (Exception e) {
            log.info("user not online", e);
        }
    }

    @Override
    public void remove(String token) {
        try {
            Long id = repository.selectIdByToken(token);
            repository.deleteSessionById(id);
            repository.deleteIdByToken(token);
        } catch (Exception e) {
            log.info("user not online", e);
        }
    }


    /**
     * 查询session过期时间
     *
     * @param id token
     * @return timestamp
     */
    @Override
    public  Long getExpired(Long id) {
        return repository.selectSessionExpiredById(id);
    }

    /**
     * 根据token获取session id
     *
     * @param token token
     * @return id
     */
    @Override
    public Long getSessionId(String token) {
        return repository.selectIdByToken(token);
    }

    public SessionRepository getRepository(){
        return this.repository;
    }
}
