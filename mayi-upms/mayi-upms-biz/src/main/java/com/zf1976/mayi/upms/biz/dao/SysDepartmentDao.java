package com.zf1976.mayi.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 部门(SysDept)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:03
 */
@Repository
public interface SysDepartmentDao extends BaseMapper<SysDepartment> {

    /**
     * 获取角色部门
     *
     * @param roleId 角色id
     * @return dept set
     */
    List<SysDepartment> selectListByRoleId(@Param("roleId") long roleId);

    /**
     * 获取子部门
     *
     * @param id 部门id
     * @return 部门列表
     */
    List<SysDepartment> selectChildrenById(@Param("id")long id);

    /**
     * 删除角色相关部门
     *
     * @param ids ids
     */
    void deleteRoleRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询部门跟角色依赖关系
     *
     * @param id id
     * @return /
     */
    Long selectDependsOnById(@Param("id") long id);

    /**
     * 查询依赖关系role id 集合
     *
     * @param id department id
     * @return ids
     */
    List<Long> selectRoleRelationById(@Param("id") long id);

    /**
     * 根据id集合查询角色依赖
     *
     * @param ids ids
     * @return 角色id集合
     */
    List<Long> selectRoleRelationByIds(@Param("ids") Collection<Long> ids);

}
