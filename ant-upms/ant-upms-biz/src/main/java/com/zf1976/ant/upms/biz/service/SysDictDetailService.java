package com.zf1976.ant.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.security.support.session.SessionContextHolder;
import com.zf1976.ant.upms.biz.dao.SysDictDao;
import com.zf1976.ant.upms.biz.dao.SysDictDetailDao;
import com.zf1976.ant.upms.biz.pojo.po.SysDict;
import com.zf1976.ant.upms.biz.pojo.po.SysDictDetail;
import com.zf1976.ant.upms.biz.convert.SysDictDetailConvert;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDetailDTO;
import com.zf1976.ant.upms.biz.pojo.query.DictDetailQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictDetailVO;
import com.zf1976.ant.upms.biz.exception.enums.DictState;
import com.zf1976.ant.upms.biz.exception.DictException;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

/**
 * 数据字典详情(SysDictDetail)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:46:00
 */
@Service
public class SysDictDetailService extends AbstractService<SysDictDetailDao, SysDictDetail> {

    private final SysDictDao sysDictDao;
    private final SysDictDetailConvert convert;

    public SysDictDetailService(SysDictDao sysDictDao) {
        this.sysDictDao = sysDictDao;
        this.convert = SysDictDetailConvert.INSTANCE;
    }

    /**
     * 按条件查询字典详情
     *
     * @param requestPage page param
     * @return dict details page
     */
    @CachePut(namespace = Namespace.DICT_DETAIL, key = "#requestPage")
    public IPage<DictDetailVO> selectDictDetailPage(RequestPage<DictDetailQueryParam> requestPage) {
        DictDetailQueryParam param = requestPage.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        LambdaQueryChainWrapper<SysDictDetail> lambdaQuery = super.lambdaQuery();
        String dictName = param.getDictName();
        if (!StringUtils.isEmpty(dictName)) {
            SysDict sysDict = ChainWrappers.lambdaQueryChain(this.sysDictDao)
                                           .eq(SysDict::getDictName, dictName)
                                           .oneOpt()
                                           .orElseThrow(() -> new DictException(DictState.DICT_NOT_FOUND));
            Long dictId = sysDict.getId();
            lambdaQuery.eq(SysDictDetail::getDictId, dictId);
        }
        lambdaQuery.like(param.getLabel() != null, SysDictDetail::getLabel, param.getLabel());
        IPage<SysDictDetail> sourcePage = super.selectPage(requestPage, lambdaQuery);
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 新增字典详情
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict(namespace = Namespace.DICT_DETAIL)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> saveDictDetail(DictDetailDTO dto) {
        SysDictDetail sysDictDetail = convert.toEntity(dto);
//        final String principal = SecurityContextHolder.getPrincipal();
//        sysDictDetail.setCreateBy(principal);
        sysDictDetail.setCreateTime(new Date());
        super.savaEntity(sysDictDetail);
        return Optional.empty();
    }

    /**
     * 更新字典详情
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict(namespace = Namespace.DICT_DETAIL)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateDictDetail(DictDetailDTO dto) {
        SysDictDetail sysDictDetail = super.lambdaQuery()
                                           .eq(SysDictDetail::getId, dto.getId())
                                           .eq(SysDictDetail::getDictId, dto.getDictId())
                                           .oneOpt()
                                           .orElseThrow(() -> new DictException(DictState.DICT_NOT_FOUND));
        this.convert.copyProperties(dto, sysDictDetail);
        String username = SessionContextHolder.username();
        sysDictDetail.setCreateBy(username);
        sysDictDetail.setUpdateTime(new Date());
        super.updateEntityById(sysDictDetail);
        return Optional.empty();
    }

    /**
     * 删除字典详情
     *
     * @param id id
     * @return /
     */
    @CacheEvict(namespace = Namespace.DICT_DETAIL)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deleteDictDetail(Long id) {
        if (!super.removeById(id)) {
            throw new DictException(DictState.DICT_NOT_FOUND);
        }
        return Optional.empty();
    }
}
