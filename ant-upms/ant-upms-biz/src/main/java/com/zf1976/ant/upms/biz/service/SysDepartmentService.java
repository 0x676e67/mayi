package com.zf1976.ant.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.zf1976.ant.common.component.load.annotation.CacheEvict;
import com.zf1976.ant.common.component.load.annotation.CachePut;
import com.zf1976.ant.common.core.constants.Namespace;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.security.support.session.SessionContextHolder;
import com.zf1976.ant.upms.biz.pojo.query.RequestPage;
import com.zf1976.ant.upms.biz.convert.DepartmentConvert;
import com.zf1976.ant.upms.biz.dao.SysDepartmentDao;
import com.zf1976.ant.upms.biz.exception.DepartmentException;
import com.zf1976.ant.upms.biz.exception.enums.DepartmentState;
import com.zf1976.ant.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.ant.upms.biz.pojo.po.SysDepartment;
import com.zf1976.ant.upms.biz.pojo.query.DeptQueryParam;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentExcelVO;
import com.zf1976.ant.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.ant.upms.biz.service.base.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * 部门(SysDept)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:57
 */
@Service
public class SysDepartmentService extends AbstractService<SysDepartmentDao, SysDepartment> {

    private final DepartmentConvert convert = DepartmentConvert.INSTANCE;

    /**
     * 按条件分页查询部门
     *
     * @param requestPage page param
     * @return dept list page
     */
    @CachePut(namespace = Namespace.DEPARTMENT, key = "#requestPage")
    public IPage<DepartmentVO> selectDeptPage(RequestPage<DeptQueryParam> requestPage) {
        IPage<SysDepartment> sourcePage = super.queryChain()
                                               .setQueryParam(requestPage)
                                               .selectPage();
        return this.deptTreeBuilder(sourcePage);
    }

    /**
     * 排除本级部门所在部门树
     *
     * @param id dept id
     * @return 满足前提条件的部门树
     */
    @CachePut(namespace = Namespace.DEPARTMENT, key = "#id")
    public IPage<DepartmentVO> selectDeptVertex(Long id) {
        // 记录是否存在
        super.lambdaQuery()
             .eq(SysDepartment::getId, id)
             .oneOpt().orElseThrow(() -> new BusinessException(BusinessMsgState.DATA_NOT_FOUNT));
        // 获取查询页
        IPage<SysDepartment> sourcePage = super.queryChain()
                                               .setQueryParam(new RequestPage<>())
                                               .selectPage();
        // 收集下级部门id集合
        Set<Long> nextLowDeptIds = this.collectCurrentChildrenDeptIds(id, null, HashSet::new);
        // 过滤掉下级部门 以及本部门
        final List<SysDepartment> collect = sourcePage.getRecords()
                                                      .stream()
                                                      .filter(sysDept -> !nextLowDeptIds.contains(sysDept.getId()))
                                                      .sorted(Comparator.comparingInt(SysDepartment::getDeptSort))
                                                      .collect(Collectors.toList());
        // 构建部门树并返回
        return this.deptTreeBuilder(sourcePage.setRecords(collect));
    }

    /**
     * 收集顶点
     *
     * @param currentDept 部门
     * @param deptList 部门列表
     * @return /
     */
    private List<SysDepartment> collectVertex(SysDepartment currentDept, List<SysDepartment> deptList) {
        if (ObjectUtils.isEmpty(currentDept) || ObjectUtils.isEmpty(currentDept.getPid())) {
            List<SysDepartment> var3 = super.lambdaQuery()
                                            .isNull(SysDepartment::getPid)
                                            .list();
            // 顶级部门
            deptList.addAll(var3);
            return deptList;
        }
        // 查询同级部门 同时包含本部门
        List<SysDepartment> var1 = super.lambdaQuery()
                                        .eq(SysDepartment::getPid, currentDept.getPid())
                                        .list();
        deptList.addAll(var1);
        // 获取上级部门
        SysDepartment var2 = super.lambdaQuery()
                                  .eq(SysDepartment::getId, currentDept.getPid())
                                  .one();
        return collectVertex(var2, deptList);
    }

    /**
     * 构建部门树
     *
     * @param sourcePage source page
     */
    private IPage<DepartmentVO> deptTreeBuilder(IPage<SysDepartment> sourcePage) {
        final IPage<DepartmentVO> targetPage = super.mapPageToTarget(sourcePage, this.convert::toVo);
        // 所有节点
        final List<DepartmentVO> vertex = targetPage.getRecords();
        // 已被添加的节点
        List<DepartmentVO> childrenVertex = new LinkedList<>();
        // 构建tree
        vertex.forEach(var1 -> {
            vertex.stream()
                  .filter(var2 -> var2.getPid() != null && !ObjectUtils.nullSafeEquals(var1.getId(), var2.getId()) && ObjectUtils.nullSafeEquals(var1.getId(), var2.getPid()))
                  .forEach(var2 -> {
                      if (CollectionUtils.isEmpty(var1.getChildren())) {
                          var1.setChildren(new LinkedList<>());
                      }
                      var1.getChildren().add(var2);
                      childrenVertex.add(var2);
                  });
            // 设置dept tree properties
            this.setDeptTreeProperties(var1);
        });
        // 清除已被添加的节点
        vertex.removeAll(childrenVertex);
        final List<DepartmentVO> target = vertex.stream()
                                                .sorted(Comparator.comparingInt(DepartmentVO::getDeptSort))
                                                .collect(Collectors.toList());
        return targetPage.setRecords(Collections.unmodifiableList(target));
    }

    /**
     * 设置 tree properties
     *
     * @param vo vo
     */
    private void setDeptTreeProperties(DepartmentVO vo) {
        final boolean empty = CollectionUtils.isEmpty(vo.getChildren());
        vo.setHasChildren(!empty);
        vo.setLeaf(empty);
    }

    /**
     * 新增部门
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict(namespace = Namespace.DEPARTMENT, dependsOn = {Namespace.ROLE, Namespace.USER})
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> savaDept(DepartmentDTO dto) {
        // 确认部门是否存在
        super.lambdaQuery()
             .eq(SysDepartment::getName, dto.getName())
             .oneOpt()
             .ifPresent(sysDept -> {
                 throw new DepartmentException(DepartmentState.DEPARTMENT_EXISTING, dto.getName());
             });
        SysDepartment sysDept = this.convert.toEntity(dto);
        String username = SessionContextHolder.username();
        sysDept.setCreateBy(username);
        sysDept.setCreateTime(new Date());
        super.savaEntity(sysDept);
        return Optional.empty();
    }

    /**
     * 更新部门
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict(namespace = Namespace.DEPARTMENT, dependsOn = {Namespace.ROLE, Namespace.USER})
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> updateDept(DepartmentDTO dto) {

        // 确认部门是否存在
        SysDepartment sysDept = super.lambdaQuery()
                                     .eq(SysDepartment::getId, dto.getId())
                                     .oneOpt().orElseThrow(() -> new DepartmentException(DepartmentState.DEPARTMENT_NOT_FOUND));
        // 确认部门是否已存在
        if (!ObjectUtils.nullSafeEquals(dto.getName(), sysDept.getName())) {
            super.lambdaQuery()
                 .ne(SysDepartment::getId, sysDept.getId())
                 .eq(SysDepartment::getName, dto.getName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new DepartmentException(DepartmentState.DEPARTMENT_EXISTING, dto.getName());
                 });
        }
        // 父部门及以下所有部门关闭
        if (!ObjectUtils.isEmpty(dto.getEnabled()) && !dto.getEnabled()) {
            // 关闭子部门
            super.lambdaQuery()
                 .eq(SysDepartment::getPid, dto.getId())
                 .list()
                 .forEach(dept -> {
                     dept.setEnabled(dto.getEnabled());
                     super.updateEntityById(dept);
                     this.closeChildrenDept(dept.getId(), dto.getEnabled());
                 });
        }
        // 禁止状态下禁止修改
        if (!ObjectUtils.isEmpty(dto.getPid())) {
            final SysDepartment parentDept = super.lambdaQuery()
                                                  .select(SysDepartment::getEnabled)
                                                  .eq(SysDepartment::getId, dto.getPid())
                                                  .oneOpt().orElseThrow(() -> new DepartmentException(DepartmentState.DEPARTMENT_NOT_FOUND));
            if (!parentDept.getEnabled()) {
                throw new DepartmentException(DepartmentState.DEPARTMENT_PARENT_CLOSE);
            }
        }
        /*
         * 禁止设置本级部门成为子部门的部门
         * 获取所有子部门id集合
         */
        final Set<Long> childrenDeptIds = this.collectCurrentChildrenDeptIds(sysDept.getId(), null, HashSet::new);
        // 确认预设置pid 是否为子部门id
        if (!CollectionUtils.isEmpty(childrenDeptIds) && !ObjectUtils.isEmpty(dto.getPid())) {
            if (childrenDeptIds.contains(dto.getPid())) {
                throw new DepartmentException(DepartmentState.DEPARTMENT_BAN_PARENT);
            }
        }
        // 禁止设置上级部门为本级部门
        if (ObjectUtils.nullSafeEquals(dto.getPid(), sysDept.getId())) {
            throw new DepartmentException(DepartmentState.DEPARTMENT_BAN_CURRENT);
        }
        this.update(dto, sysDept);
        return Optional.empty();
    }

    private void update(DepartmentDTO dto, SysDepartment currentDept) {
        this.convert.copyProperties(dto, currentDept);
        String username = SessionContextHolder.username();
        currentDept.setCreateBy(username);
        currentDept.setUpdateTime(new Date());
        super.updateEntityById(currentDept);
    }

    /**
     * 删除 部门包含所有子部门
     *
     * @param ids ids
     * @return /
     */
    @CacheEvict(namespace = Namespace.DEPARTMENT,dependsOn = {Namespace.ROLE, Namespace.USER})
    @Transactional(rollbackFor = Exception.class)
    public Optional<Void> deleteDeptList(Set<Long> ids) {
        final Set<Long> treeIds = this.collectCurrentDeptTreeIds(ids, HashSet::new);
        if (!CollectionUtils.isEmpty(treeIds)) {
            treeIds.forEach(id -> {
                if (super.baseMapper.selectDependsOnById(id) > 0) {
                    throw new DepartmentException(DepartmentState.DEPARTMENT_DEPENDS_ERROR);
                }
            });
        }
        // 删除department
        super.deleteByIds(treeIds);
        // 删除 role-department
        super.baseMapper.deleteRoleRelationByIds(ids);
        return Optional.empty();
    }

    /**
     * 收集本级部门以及所有子部门id集合
     *
     * @param ids id collection
     */
    private Set<Long> collectCurrentDeptTreeIds(Set<Long> ids, Supplier<Set<Long>> supplier) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        Assert.notNull(supplier, "collectionIds can not been null");
        supplier.get().addAll(ids);
        Set<Long> set = supplier.get();
        set.addAll(ids);
        ids.forEach(id -> {
            // 子部门id集合
            final Set<Long> collectIds = this.collectNextLowDeptIds(id, null);
            // 收集子部门id集合
            this.collectCurrentDeptTreeIds(collectIds, supplier);
        });
        return set;
    }

    /**
     * 收集本级部门下所有子部门id集合
     *
     * @param id current dept id
     * @param condition status
     * @param supplier supplied object
     * @return ids
     */
    private Set<Long> collectCurrentChildrenDeptIds(long id, Boolean condition, Supplier<Set<Long>> supplier) {
        // 子部门id集合
        final Set<Long> childrenIds = this.collectNextLowDeptIds(id, condition);
        if (!CollectionUtils.isEmpty(childrenIds)) {
            // collect
            supplier.get().addAll(childrenIds);
            // 继续往下子部门收集
            childrenIds.forEach(childrenId -> {
                this.collectCurrentChildrenDeptIds(childrenId, condition, supplier);
            });
        }
        return supplier.get();
    }

    /**
     * 收集下级子部门id集合
     * @param id id
     * @param condition status
     * @return children ids
     */
    private Set<Long> collectNextLowDeptIds(Long id, Boolean condition) {
        final LambdaQueryChainWrapper<SysDepartment> lambdaQuery = super.lambdaQuery();
        if (condition != null) {
            lambdaQuery.eq(SysDepartment::getEnabled, condition);
        }
        // 子部门id集合
        return lambdaQuery.eq(SysDepartment::getPid, id)
                          .list()
                          .stream()
                          .map(SysDepartment::getId)
                          .collect(Collectors.toSet());
    }

    /**
     * 设置所有子部门关闭
     *
     * @param id 部门id
     * @param closeValue value
     */
    private void closeChildrenDept(Long id, boolean closeValue) {
        //获取子部门
        super.lambdaQuery()
             .eq(SysDepartment::getPid, id)
             .list()
             .forEach(var2 -> {
                 var2.setEnabled(closeValue);
                 super.updateEntityById(var2);
                 this.closeChildrenDept(var2.getId(), closeValue);
             });
    }

    /**
     * 是否存在子部门
     *
     * @param id parent id
     * @return boolean
     */
    private boolean hasChildrenDept(long id) {
        final Integer count = super.lambdaQuery()
                                   .eq(SysDepartment::getPid, id)
                                   .count();
        return count > 0;
    }

    /**
     * 确认下一级部门 开启状态
     *
     * @param id id
     * @return boolean
     */
    private boolean childrenDeptStatus(long id, boolean status) {
        final long count = super.lambdaQuery()
                                .eq(SysDepartment::getPid, id)
                                .eq(SysDepartment::getEnabled, status)
                                .count();
        return count > 0;
    }

    /**
     * 下载部门信息
     *
     * @param requestPage request
     * @param response    response
     * @return /
     */
    public Optional<Void> downloadExcelDept(RequestPage<DeptQueryParam> requestPage, HttpServletResponse response) {
        List<SysDepartment> departmentList = super.queryChain()
                                                  .setQueryParam(requestPage)
                                                  .selectList();
        final List<DepartmentVO> deptTree = super.mapListToTarget(departmentList, this.convert::toVo);
        final Collection<Map<String, Object>> mapCollection = this.downloadDeptTreeBuilder(new LinkedList<>(), deptTree);
        super.downloadExcel(mapCollection, response);
        return Optional.empty();
    }

    private Collection<Map<String, Object>> downloadDeptTreeBuilder(Collection<Map<String,Object>> mapCollection, Collection<DepartmentVO> deptTree) {
        deptTree.forEach(var0 -> {
            Map<String,Object> map = new HashMap<>(16);
            final DepartmentExcelVO downloadVo = new DepartmentExcelVO();
            downloadVo.setName(var0.getName());
            downloadVo.setPid(var0.getPid());
            downloadVo.setEnabled(var0.getEnabled());
            downloadVo.setCreateTime(var0.getCreateTime());
            if (!CollectionUtils.isEmpty(var0.getChildren())) {
                downloadVo.setChildren(this.downloadDeptTreeBuilder(new LinkedList<>(), var0.getChildren()));
            }
            super.setProperties(map, downloadVo);
            mapCollection.add(map);
        });
        return mapCollection;
    }
}
