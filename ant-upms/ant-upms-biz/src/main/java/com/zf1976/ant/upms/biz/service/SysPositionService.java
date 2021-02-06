package com.zf1976.ant.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.ant.common.component.load.annotation.CaffeinePut;
import com.zf1976.ant.common.component.load.annotation.CaffeineEvict;
import com.zf1976.ant.common.component.load.annotation.Space;
import com.zf1976.ant.upms.biz.pojo.po.SysPosition;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import com.zf1976.ant.upms.biz.convert.SysPositionConvert;
import com.zf1976.ant.upms.biz.dao.SysPositionDao;
import com.zf1976.ant.upms.biz.dao.SysUserDao;
import com.zf1976.ant.common.core.foundation.query.RequestPage;
import com.zf1976.ant.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.ant.upms.biz.pojo.query.PositionQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.job.JobExcelVO;
import com.zf1976.ant.upms.biz.pojo.vo.job.PositionVO;
import com.zf1976.ant.common.security.safe.SecurityContextHolder;
import com.zf1976.ant.upms.biz.exp.enums.JobState;
import com.zf1976.ant.upms.biz.exp.enums.UserState;
import com.zf1976.ant.upms.biz.exp.JobException;
import com.zf1976.ant.upms.biz.exp.UserException;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 岗位(SysJob)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:58
 */
@Service
public class SysPositionService extends AbstractService<SysPositionDao, SysPosition> {

    private final SysUserDao sysUserDao;

    private final SysPositionConvert convert;

    public SysPositionService(SysUserDao sysUserDao) {
        this.sysUserDao = sysUserDao;
        this.convert = SysPositionConvert.INSTANCE;
    }

    /**
     * 按条件查询岗位
     *
     * @param requestPage page param
     * @return job list
     */
    @CaffeinePut(namespace = Space.JOB, key = "#requestPage")
    public IPage<PositionVO> selectPositionPage(RequestPage<PositionQueryParam> requestPage) {
        IPage<SysPosition> sourcePage = this.queryChain()
                                            .setQueryParam(requestPage)
                                            .selectPage();
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 查询用户所有岗位
     *
     * @param id id
     * @return job ids
     */
    public Set<Long> selectUserPosition(Long id) {
        ChainWrappers.lambdaQueryChain(this.sysUserDao)
                     .eq(SysUser::getId, id)
                     .oneOpt().orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
        return super.baseMapper.selectListByUserId(id)
                               .stream()
                               .map(SysPosition::getId)
                               .collect(Collectors.toSet());
    }

    /**
     * 新增岗位
     *
     * @param dto dto
     * @return /
     */
    @CaffeineEvict(namespace = Space.JOB, dependsOn = "user")
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> savePosition(PositionDTO dto) {
        super.lambdaQuery()
             .eq(SysPosition::getName, dto.getName())
             .oneOpt()
             .ifPresent(sysJob -> {
                 throw new JobException(JobState.JOB_EXISTING, sysJob.getName());
             });
        final String principal = SecurityContextHolder.getPrincipal();
        SysPosition sysJob = convert.toEntity(dto);
        sysJob.setCreateBy(principal);
        sysJob.setCreateTime(new Date());
        super.savaEntity(sysJob);
        return Optional.empty();
    }

    /**
     * 更新岗位
     *
     * @param dto dto
     * @return /
     */
    @CaffeineEvict(namespace = Space.JOB, dependsOn = "user")
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updatePosition(PositionDTO dto) {
        // 查询更新岗位是否存在
        final SysPosition sysJob = super.lambdaQuery()
                                        .eq(SysPosition::getId, dto.getId())
                                        .oneOpt().orElseThrow(() -> new JobException(JobState.JOB_NOT_FOUND));
        if (!ObjectUtils.nullSafeEquals(sysJob.getName(), dto.getName())) {
            // 确认岗位名是否存在
            super.lambdaQuery()
                 .eq(SysPosition::getName, dto.getName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new JobException(JobState.JOB_EXISTING, var1.getName());
                 });
        }
        final String principal = SecurityContextHolder.getPrincipal();
        this.convert.copyProperties(dto, sysJob);
        sysJob.setUpdateBy(principal);
        sysJob.setUpdateTime(new Date());
        super.updateEntityById(sysJob);
        return Optional.empty();
    }

    /**
     * 删除岗位
     *
     * @param ids id collection
     * @return /
     */
    @CaffeineEvict(namespace = Space.JOB, dependsOn = "user")
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deletePositionList(Set<Long> ids) {
        super.deleteByIds(ids);
        super.baseMapper.deleteUserRelationById(ids);
        return Optional.empty();
    }

    /**
     * 下载excel岗位信息
     *
     * @param requestPage request page
     * @param response response
     * @return /
     */
    public Optional<Void> downloadPositionExcel(RequestPage<PositionQueryParam> requestPage, HttpServletResponse response) {
        List<SysPosition> records = super.queryChain()
                                         .setQueryParam(requestPage)
                                         .selectList();
        List<Map<String,Object>> mapList = new LinkedList<>();
        records.forEach(sysJob -> {
            Map<String, Object> map = new LinkedHashMap<>();
            JobExcelVO downloadJobVo = new JobExcelVO();
            downloadJobVo.setName(sysJob.getName());
            downloadJobVo.setEnabled(sysJob.getEnabled());
            downloadJobVo.setCreateBy(sysJob.getCreateBy());
            downloadJobVo.setCreateTime(sysJob.getCreateTime());
            super.setProperties(map,downloadJobVo);
            mapList.add(map);
        });
        super.downloadExcel(mapList, response);
        return Optional.empty();
    }
}
