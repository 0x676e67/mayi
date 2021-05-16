package com.zf1976.ant.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.ant.upms.biz.pojo.po.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 角色表(SysRole)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:04
 */
@Repository
public interface SysRoleDao extends BaseMapper<SysRole> {

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return roles
     */
    List<SysRole> selectListByUserId(@Param("userId") long userId);

    /**
     * 获取用户角色
     *
     * @param username 用户名
     * @return roles
     */
    List<SysRole> selectListByUsername(@Param("username") String username);

    /**
     * 删除角色相关菜单
     *
     * @param ids ids
     */
    void deleteMenuRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 删除角色相关部门
     *
     * @param ids ids
     */
    void deleteDepartmentRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 删除用户依赖关系
     *
     * @param ids ids
     */
    void deleteUserRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询角色依赖关系
     *
     * @param id id
     * @return /
     */
    Long selectUserDependsOnById(@Param("id") long id);

    /**
     * 添加角色与部门依赖关系
     *
     * @param id            id
     * @param departmentIds 部门id
     */
    void saveDepartmentRelationById(@Param("id") long id, @Param("departmentIds") Collection<Long> departmentIds);

    /**
     * 添加角色与菜单依赖关系
     *
     * @param id      id
     * @param menuIds menu id collection
     */
    void saveMenuRelationById(@Param("id") long id, @Param("menuIds") Collection<Long> menuIds);

}
