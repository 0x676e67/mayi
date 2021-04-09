package com.zf1976.ant.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.common.security.support.session.RedisSessionHolder;
import com.zf1976.ant.common.security.support.session.repository.SessionRepository;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.core.util.RedisUtils;
import com.zf1976.ant.common.core.util.StringUtils;
import com.zf1976.ant.upms.biz.feign.SecurityClient;
import com.zf1976.ant.common.security.property.SecurityProperties;
import com.zf1976.ant.upms.biz.convert.SessionConvert;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.query.SessionQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.SessionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/1/20
 **/
@Slf4j(topic = "[online]-session")
@Service
public class SysOnlineService {

    private final SecurityProperties securityProperties;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final SessionRepository sessionRepository;
    private final static String PATTERN_SUFFIX = "*";
    private final SessionConvert sessionConvert = SessionConvert.INSTANCE;
    private final RestTemplate restTemplate = new RestTemplate();
    private final SecurityClient securityClient;
    private ClientHttpRequest clientHttpRequest;

    public SysOnlineService(RedisTemplate<Object, Object> template, SecurityProperties securityConfig, SessionRepository sessionRepository, SecurityClient securityClient) {
        this.redisTemplate = template;
        this.securityProperties = securityConfig;
        this.sessionRepository = sessionRepository;
        this.securityClient = securityClient;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(redisTemplate, "repository cannot be null");
        Assert.notNull(securityProperties, "config cannot be null");
    }


    public IPage<SessionVO> selectSessionPage(RequestPage<SessionQueryParam> requestPage){
        final Set<Long> sessionIdList = this.getOnlinePageSessionIds(requestPage);
        final SessionQueryParam param = requestPage.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        final List<SessionVO> vos = this.sessionRepository.selectListByIds(sessionIdList)
                                                          .stream()
                                                          .map(sessionConvert::toVO)
                                                          .filter(vo -> {
                                                              if (param.getFilter() != null) {
                                                                  return this.getKeyword(vo)
                                                                             .contains(param.getFilter());
                                                              }
                                                              return true;
                                                          })
                                                          .collect(Collectors.toList());
        final Page<SessionVO> page = new Page<>(requestPage.getPage(), requestPage.getSize(), vos.size());
        return page.setRecords(vos);
    }

    public Set<Long> getOnlinePageSessionIds() {
        return RedisUtils.scanKeys(this.getPatternSessionId())
                         .stream()
                         .map(StringUtils::getNumber)
                         .collect(Collectors.toSet());
    }

    public Set<Long> getOnlinePageSessionIds(RequestPage<SessionQueryParam> requestPage) {
        final int page = requestPage.getPage();
        final int pageSize = requestPage.getSize();
        return RedisUtils.scanKeysForPage(this.getPatternSessionId(), page, pageSize)
                         .stream()
                         .map(StringUtils::getNumber)
                         .collect(Collectors.toSet());
    }

    private String getKeyword(SessionVO vo) {
        final StringBuilder builder = new StringBuilder();
        for (Field field : vo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            builder.append(ReflectionUtils.getField(field, vo));
        }
        return builder.toString();
    }

    private String getPatternSessionId() {
        return securityProperties.getPrefixSessionId() + PATTERN_SUFFIX;
    }

    private ClientHttpRequest createLogoutRequest() {
        if (this.clientHttpRequest == null) {
            synchronized (this) {
                if (this.clientHttpRequest == null) {
                    var requestFactory = this.restTemplate.getRequestFactory();
                    try {
                        this.clientHttpRequest = requestFactory.createRequest(URI.create(securityProperties.getLogoutUrl()),HttpMethod.POST);
                    } catch (IOException e) {
                        log.error("create logout request client fail：{}", e.getMessage());
                    }
                }
            }
        }
        return this.clientHttpRequest;
    }

    private boolean executeLogout(String token) {
        var logoutRequest = createLogoutRequest();
        logoutRequest.getHeaders()
                     .add(HttpHeaders.AUTHORIZATION, token);
        try {
            var response = logoutRequest.execute();
            return response.getStatusCode()
                           .is2xxSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Optional<Void> forceOffline(Set<Long> ids) {
        // 操作方
        Long sessionId = RedisSessionHolder.getSessionId();
        ids.forEach(id -> {
            // 不允许强制自己离线
            if (!ObjectUtils.nullSafeEquals(id, sessionId)){
                // 调用远程服务进行登出处理
                var logout = this.securityClient.logout();
                Assert.isTrue(logout.getSuccess(),"this session not online");
            }
        });
        return Optional.empty();
    }
}
