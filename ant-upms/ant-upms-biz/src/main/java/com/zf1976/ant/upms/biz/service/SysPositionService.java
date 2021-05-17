package com.zf1976.ant.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.component.load.annotation.CacheConfig;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import com.zf1976.ant.upms.biz.convert.SysPositionConvert;
import com.zf1976.ant.upms.biz.dao.SysPositionDao;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.ant.upms.biz.pojo.query.PositionQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.job.PositionExcelVO;
import com.zf1976.ant.upms.biz.pojo.vo.job.PositionVO;
import com.zf1976.ant.upms.biz.exception.enums.PositionState;
import com.zf1976.ant.upms.biz.exception.PositionException;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 岗位(SysJob)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:58
 */
@Service
@CacheConfig(namespace = Namespace.POSITION, dependsOn = Namespace.USER)
public class SysPositionService extends AbstractService<SysPositionDao, SysPosition> {

    private final SysPositionConvert convert = SysPositionConvert.INSTANCE;

    /**
     * 按条件查询岗位
     *
     * @param query page param
     * @return job list
     */
    @CachePut(key = "#query")
    public IPage<PositionVO> selectPositionPage(Query<PositionQueryParam> query) {
        IPage<SysPosition> sourcePage = this.queryWrapper()
                                            .chainQuery(query)
                                            .selectPage();
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 新增岗位
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void savePosition(PositionDTO dto) {
        super.lambdaQuery()
             .eq(SysPosition::getName, dto.getName())
             .oneOpt()
             .ifPresent(sysJob -> {
                 throw new PositionException(PositionState.POSITION_EXISTING, sysJob.getName());
             });
        SysPosition sysJob = convert.toEntity(dto);
        super.savaOrUpdate(sysJob);
        return null;
    }

    /**
     * 更新岗位
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void updatePosition(PositionDTO dto) {
        // 查询更新岗位是否存在
        final SysPosition sysPosition = super.lambdaQuery()
                                        .eq(SysPosition::getId, dto.getId())
                                        .oneOpt().orElseThrow(() -> new PositionException(PositionState.POSITION_NOT_FOUND));
        if (!ObjectUtils.nullSafeEquals(sysPosition.getName(), dto.getName())) {
            // 确认岗位名是否存在
            super.lambdaQuery()
                 .eq(SysPosition::getName, dto.getName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new PositionException(PositionState.POSITION_EXISTING, var1.getName());
                 });
        }
        // 复制属性
        this.convert.copyProperties(dto, sysPosition);
        super.savaOrUpdate(sysPosition);
        return null;
    }

    /**
     * 删除岗位
     *
     * @param ids id collection
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deletePosition(Set<Long> ids) {
        super.deleteByIds(ids);
        super.baseMapper.deleteRelationByIds(ids);
        return Optional.empty();
    }

    /**
     * 下载excel岗位信息
     *
     * @param query request page
     * @param response response
     * @return /
     */
    public Void downloadPositionExcel(Query<PositionQueryParam> query, HttpServletResponse response) {
        List<SysPosition> records = super.queryWrapper()
                                         .chainQuery(query)
                                         .selectList();
        List<Map<String,Object>> mapList = new LinkedList<>();
        records.forEach(sysJob -> {
            Map<String, Object> map = new LinkedHashMap<>();
            PositionExcelVO downloadJobVo = new PositionExcelVO();
            downloadJobVo.setName(sysJob.getName());
            downloadJobVo.setEnabled(sysJob.getEnabled());
            downloadJobVo.setCreateBy(sysJob.getCreateBy());
            downloadJobVo.setCreateTime(sysJob.getCreateTime());
            super.setProperties(map,downloadJobVo);
            mapList.add(map);
        });
        super.downloadExcel(mapList, response);
        return null;
    }
}
