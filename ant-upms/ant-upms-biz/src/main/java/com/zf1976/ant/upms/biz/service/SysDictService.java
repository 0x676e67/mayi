package com.zf1976.ant.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.security.support.session.DistributedSessionManager;
import com.zf1976.ant.upms.biz.pojo.po.SysDict;
import com.zf1976.ant.upms.biz.pojo.po.SysDictDetail;
import com.zf1976.ant.upms.biz.convert.SysDictConvert;
import com.zf1976.ant.upms.biz.dao.SysDictDao;
import com.zf1976.ant.upms.biz.dao.SysDictDetailDao;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.ant.upms.biz.pojo.query.DictQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictDownloadVO;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictVO;
import com.zf1976.ant.upms.biz.exception.enums.DictState;
import com.zf1976.ant.upms.biz.exception.DictException;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 数据字典(SysDict)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:59
 */
@Service
public class SysDictService extends AbstractService<SysDictDao, SysDict> {

    private final SysDictDetailDao sysDictDetailDao;
    private final SysDictConvert convert;

    public SysDictService(SysDictDetailDao sysDictDetailDao) {
        this.sysDictDetailDao = sysDictDetailDao;
        this.convert = SysDictConvert.INSTANCE;
    }

    /**
     * 按条件查询字典页
     *
     * @param requestPage page param
     * @return dict list
     */
    @CachePut(namespace = Namespace.DICT, key = "#requestPage")
    public IPage<DictVO> selectDictPage(RequestPage<DictQueryParam> requestPage) {
        IPage<SysDict> sourcePage = super.queryChain()
                                         .setQueryParam(requestPage)
                                         .selectPage();
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 新增字典
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict(namespace = Namespace.DICT)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> saveDict(DictDTO dto) {
        // 确认字典名是否存在
        super.lambdaQuery()
             .eq(SysDict::getDictName, dto.getDictName())
             .oneOpt()
             .ifPresent(sysDict -> {
                 throw new DictException(DictState.DICT_EXISTING, sysDict.getDictName());
             });
        SysDict sysDict = convert.toEntity(dto);
        super.savaEntity(sysDict);
        return Optional.empty();
    }

    /**
     * 更新字典
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict(namespace = Namespace.DICT)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateDict(DictDTO dto) {

        SysDict sysDict = super.lambdaQuery()
                               .eq(SysDict::getId, dto.getId())
                               .oneOpt().orElseThrow(() -> new DictException(DictState.DICT_NOT_FOUND));

        if (!ObjectUtils.nullSafeEquals(dto.getDictName(), sysDict.getDictName())) {
            // 确认字典名是否已存在
            super.lambdaQuery()
                 .eq(SysDict::getDictName, dto.getDictName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new DictException(DictState.DICT_EXISTING, var1.getDictName());
                 });
        }

        this.convert.copyProperties(dto, sysDict);
        super.updateEntityById(sysDict);
        return Optional.empty();
    }

    /**
     * 删除字典
     * @param ids id collection
     * @return /
     */
    @CacheEvict(namespace = Namespace.DICT)
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deleteDictList(Set<Long> ids) {
        super.deleteByIds(ids);
        return Optional.empty();
    }

    /**
     * 下载dict excel文件
     * @param requestPage page param
     * @param response response
     * @return /
     */
    public Optional<Void> downloadDictExcel(RequestPage<DictQueryParam> requestPage, HttpServletResponse response) {
        List<SysDict> records = super.queryChain()
                                     .setQueryParam(requestPage)
                                     .selectList();
        List<Map<String,Object>> mapList = new LinkedList<>();
        records.forEach(sysDict -> {
            List<SysDictDetail> details = new LambdaQueryChainWrapper<>(sysDictDetailDao).eq(SysDictDetail::getDictId, sysDict.getId()).list();
            details.forEach(sysDictDetail -> {
                DictDownloadVO downloadDictVo = new DictDownloadVO();
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                downloadDictVo.setDictName(sysDict.getDictName());
                downloadDictVo.setDescription(sysDict.getDescription());
                downloadDictVo.setLabel(sysDictDetail.getLabel());
                downloadDictVo.setValue(sysDictDetail.getValue());
                downloadDictVo.setCreateBy(sysDict.getCreateBy());
                downloadDictVo.setCreateTime(sysDict.getCreateTime());
                super.setProperties(map,downloadDictVo);
                mapList.add(map);
            });
        });
        super.downloadExcel(mapList,response);
        return Optional.empty();
    }
}
