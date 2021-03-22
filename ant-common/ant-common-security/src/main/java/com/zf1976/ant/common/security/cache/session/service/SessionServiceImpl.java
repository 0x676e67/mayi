package com.zf1976.ant.common.security.cache.session.service;

import com.zf1976.ant.common.security.AntUserDetails;
import com.zf1976.ant.common.security.SecurityProperties;
import com.zf1976.ant.common.security.cache.session.Session;
import com.zf1976.ant.common.security.cache.session.repository.SessionRepository;
import com.zf1976.ant.common.security.pojo.vo.DepartmentVo;
import com.zf1976.ant.common.core.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
    public void save(String token, AntUserDetails userDetails) {
        try {
            HttpServletRequest request = RequestUtils.getRequest();
            Long id = userDetails.getId();
            DepartmentVo department = userDetails.getUserInfo()
                                                 .getDepartment();
            String deptName = department == null ? null : department.getName();
            Session session = new Session();
            session.setId(id)
                   .setLoginTime(new Date())
                   .setUsername(userDetails.getUsername())
                   .setNickName(userDetails.getUserInfo().getNickName())
                   .setOwner(ObjectUtils.nullSafeEquals(userDetails.getUsername(), securityProperties.getOwner()))
                   .setDataPermission(new ArrayList<>(userDetails.getDataScopes()))
                   .setPermission(new ArrayList<>(userDetails.getPermission()))
                   .setToken(token)
                   .setDepartment(deptName)
                   .setIp(RequestUtils.getIpAddress(request))
                   .setIpRegion(RequestUtils.getIpRegion(request))
                   .setBrowser(RequestUtils.getBrowser(request))
                   .setOperatingSystemType(RequestUtils.getOpsSystemType(request));
            repository.savaIdByToken(token, id);
            repository.saveSessionById(id, session);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void update(String token, AntUserDetails userDetails) {
        try{
            this.remove(userDetails.getId());
            this.save(token, userDetails);
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

}
