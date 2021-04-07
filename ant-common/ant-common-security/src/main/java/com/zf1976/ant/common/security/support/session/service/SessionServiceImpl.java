package com.zf1976.ant.common.security.support.session.service;

import com.zf1976.ant.common.security.support.session.Session;
import com.zf1976.ant.common.security.support.session.repository.SessionRepository;
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

    public SessionServiceImpl(SessionRepository repository) {
        this.repository = repository;
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
        var expired = getExpired(session.getId());
        this.update(token, session, expired);
    }

    @Override
    public void update(String token, Session session, Long expired) {
        try {
            var id = session.getId();
            if (repository.hasSession(id)) {
                if (expired > 0) {
                    repository.updateSessionByToken(token, session, expired);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(Long id, Session session) {
        Long expired = repository.selectSessionExpiredById(id);
        this.update(id, session, expired);
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

    @Override
    public  Long getExpired(Long id) {
        return repository.selectSessionExpiredById(id);
    }

    @Override
    public Long getId(String token) {
        return repository.selectIdByToken(token);
    }

    /**
     * get repository
     *
     * @return {link Repository}
     */
    public SessionRepository getRepository(){
        return this.repository;
    }
}
