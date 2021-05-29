package com.zf1976.ant.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.common.component.cache.annotation.CacheConfig;
import com.zf1976.ant.common.component.cache.annotation.CacheEvict;
import com.zf1976.ant.common.component.cache.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.upms.biz.convert.SysDictDetailConvert;
import com.zf1976.ant.upms.biz.dao.SysDictDao;
import com.zf1976.ant.upms.biz.dao.SysDictDetailDao;
import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDetailDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDict;
import com.zf1976.ant.upms.biz.pojo.po.SysDictDetail;
import com.zf1976.ant.upms.biz.pojo.query.DictDetailQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictDetailVO;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import com.zf1976.ant.upms.biz.service.exception.DictException;
import com.zf1976.ant.upms.biz.service.exception.enums.DictState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 数据字典详情(SysDictDetail)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:46:00
 */
@Service
@CacheConfig(namespace = Namespace.DICT)
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
     * @param query page param
     * @return dict details page
     */
    @CachePut(key = "#query")
    @Transactional(readOnly = true)
    public IPage<DictDetailVO> selectDictDetailPage(Query<DictDetailQueryParam> query) {
        DictDetailQueryParam param = query.getQuery();
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
        IPage<SysDictDetail> sourcePage = super.selectPage(query, lambdaQuery);
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 新增字典详情
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void saveDictDetail(DictDetailDTO dto) {
        SysDictDetail sysDictDetail = convert.toEntity(dto);
        super.savaOrUpdate(sysDictDetail);
        return null;
    }

    /**
     * 更新字典详情
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void updateDictDetail(DictDetailDTO dto) {
        // 查询字典细节实体
        SysDictDetail sysDictDetail = super.lambdaQuery()
                                           .eq(SysDictDetail::getId, dto.getId())
                                           .eq(SysDictDetail::getDictId, dto.getDictId())
                                           .oneOpt()
                                           .orElseThrow(() -> new DictException(DictState.DICT_NOT_FOUND));
        // 复制属性
        this.convert.copyProperties(dto, sysDictDetail);
        // 更新实体
        super.savaOrUpdate(sysDictDetail);
        return null;
    }

    /**
     * 删除字典详情
     *
     * @param id id
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void deleteDictDetail(Long id) {
        if (!super.removeById(id)) {
            throw new DictException(DictState.DICT_NOT_FOUND);
        }
        return null;
    }
}
