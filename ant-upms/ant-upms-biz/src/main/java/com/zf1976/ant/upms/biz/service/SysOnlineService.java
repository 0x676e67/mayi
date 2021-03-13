package com.zf1976.ant.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.ant.common.core.property.SecurityProperties;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.core.util.RedisUtils;
import com.zf1976.ant.common.core.util.StringUtils;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.common.security.cache.session.repository.SessionRepository;
import com.zf1976.ant.upms.biz.convert.SessionConvert;
import com.zf1976.ant.upms.biz.pojo.query.SessionQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.SessionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/1/20
 **/
@Slf4j
@Service
public class SysOnlineService {

    private final SecurityProperties securityConfig;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final SessionRepository sessionRepository;
    private final static String PATTERN_SUFFIX = "*";
    private final SessionConvert sessionConvert;

    public SysOnlineService(RedisTemplate<Object, Object> template, SecurityProperties securityConfig, SessionRepository sessionRepository) {
        this.redisTemplate = template;
        this.securityConfig = securityConfig;
        this.sessionRepository = sessionRepository;
        this.sessionConvert = SessionConvert.INSTANCE;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(redisTemplate, "repository cannot be null");
        Assert.notNull(securityConfig, "config cannot be null");
    }


    public IPage<SessionVO> selectSessionPage(RequestPage<SessionQueryParam> requestPage){
        final Set<Long> sessionIdList = this.getOnlinePageSessionIds(requestPage);
        final SessionQueryParam param = requestPage.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        final List<SessionVO> vos = this.sessionRepository.selectSessionByIds(sessionIdList)
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
        return RedisUtils.scanKeysForPage(this.getPatternSessionId(),
                page,
                pageSize)
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
        return securityConfig.getPrefixSessionId() + PATTERN_SUFFIX;
    }

    public Optional<Void> forceOffline(Set<Long> ids) {
        ids.forEach(id -> {
//            if (!ObjectUtils.nullSafeEquals(id, SecurityContextHolder.getPrincipalId())){
//                SessionContextHolder.removeSession(id);
//            }
        });
        return Optional.empty();
    }

}
