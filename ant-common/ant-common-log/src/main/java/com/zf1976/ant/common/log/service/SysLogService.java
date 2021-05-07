package com.zf1976.ant.common.log.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.log.convert.SysLogConvert;
import com.zf1976.ant.common.log.dao.SysLogDao;
import com.zf1976.ant.common.log.pojo.SysLog;
import com.zf1976.ant.common.log.pojo.enums.LogType;
import com.zf1976.ant.common.log.pojo.vo.base.AbstractLogVO;
import com.zf1976.ant.common.log.query.LogQueryParam;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/1/25
 **/
@Service
public class SysLogService extends ServiceImpl<SysLogDao, SysLog> {

    private final SysLogConvert convert = SysLogConvert.INSTANCE;

    public IPage<AbstractLogVO> selectUserLogPage(Query<LogQueryParam> query) {
        LogQueryParam param = query.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        var username = Objects.requireNonNull(SessionManagement.getSession()).getUsername();
        // 查询分页对象
        Page<SysLog> sourcePage = super.lambdaQuery()
                                       .eq(SysLog::getLogType, LogType.INFO)
                                       .eq(SysLog::getUsername, username)
                                       .page(new Page<>(query.getPage(), query.getSize()));
        return this.mapPage(sourcePage,convert::toUserLogVo);
    }

    public IPage<AbstractLogVO> selectLogPage(Query<LogQueryParam> query) {
        LogQueryParam param = query.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        if (param.getLogType() == null) {
            throw new BusinessException(BusinessMsgState.PARAM_ILLEGAL);
        }
        // 构建分页对象
        Page<SysLog> page = new Page<>(query.getPage(), query.getSize());
        // 构造查询条件
        byte range = 2;
        Page<SysLog> sourcePage;
        LambdaQueryChainWrapper<SysLog> lambdaQuery = super.lambdaQuery();
        if (param.getCreateTime() != null) {
            List<Date> createTime = param.getCreateTime();
            if (createTime.size() == range) {
                lambdaQuery.between(SysLog::getCreateTime,
                        createTime.get(0),
                        createTime.get(1));
            } else {
                throw new BusinessException(BusinessMsgState.PARAM_ILLEGAL);
            }
        }
        // 分页查询
        sourcePage = lambdaQuery.eq(SysLog::getLogType, param.getLogType())
                                .page(page);
        IPage<AbstractLogVO> targetPage = null;
        switch (param.getLogType()) {
            case INFO:
                targetPage = this.mapPage(sourcePage, convert::toVo);
                break;
            case ERROR:
                targetPage = this.mapPage(sourcePage, convert::toErrorVo);
                break;
            default:
        }
        // 进行过滤
        if (!ObjectUtils.isEmpty(targetPage) && param.getBlurry() != null) {
            List<AbstractLogVO> targetRecords = targetPage.getRecords()
                                                    .stream()
                                                    .filter(abstractLogVO -> {
                                                        if (param.getBlurry() != null) {
                                                            return this.getKeyword(abstractLogVO)
                                                                       .contains(param.getBlurry());
                                                        }
                                                        return false;
                                                    })
                                                    .collect(Collectors.toList());
            return targetPage.setRecords(targetRecords);
        }
        return targetPage;
    }

    /**
     * 分页对象拷贝
     *
     * @param sourcePage 原对象
     * @param translator func
     * @param <S>        目标对象
     * @return 转换结果
     */
    private  <S> IPage<S> mapPage(IPage<SysLog> sourcePage, Function<SysLog, S> translator) {
        List<S> target = sourcePage.getRecords()
                                   .stream()
                                   .map(translator)
                                   .collect(Collectors.toList());

        final com.baomidou.mybatisplus.extension.plugins.pagination.Page targetPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount());
        return targetPage.setRecords(target);
    }

    private String getKeyword(AbstractLogVO vo) {
        final StringBuilder builder = new StringBuilder();
        for (Field field : vo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            builder.append(ReflectionUtils.getField(field, vo));
        }
        return builder.toString();
    }

    /**
     * 删除日志
     *
     * @param ids ids
     * @return /
     */
    public Optional<Void> deleteLog(Set<Long> ids) {
        if (!super.removeByIds(ids)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        return Optional.empty();
    }

    /**
     * 删除所有错误日志
     *
     * @return /
     */
    public Optional<Void> deleteErrorLog() {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .eq(SysLog::getLogType, LogType.ERROR);
        if (!super.remove(wrapper)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        return Optional.empty();
    }

    /**
     * 删除所有常规日志
     *
     * @return /
     */
    public Optional<Void> deleteInfoLog() {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .ne(SysLog::getLogType, LogType.ERROR);
        if (!super.remove(wrapper)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        return Optional.empty();
    }

}
