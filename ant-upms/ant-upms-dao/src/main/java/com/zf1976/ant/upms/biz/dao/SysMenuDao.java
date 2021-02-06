package com.zf1976.ant.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.ant.upms.biz.pojo.po.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 系统菜单(SysMenu)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:06
 */
@Repository
public interface SysMenuDao extends BaseMapper<SysMenu> {

    /**
     * 查询角色所有菜单
     *
     * @param roleId id
     * @return 菜单列表
     */
    List<SysMenu> selectListByRoleId(@Param("roleId") long roleId);

    /**
     * 根据角色id集合查询所有菜单
     *
     * @param roleIds ids
     * @return 菜单列表
     */
    List<SysMenu> selectListByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    /**
     * 删除角色相关菜单
     *
     * @param ids ids
     */
    void deleteRoleRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询菜单跟角色依赖关系
     *
     * @param id id
     * @return count
     */
    Long selectRoleDependsOnById(@Param("id") Long id);

    /**
     * 查询菜单最大类型值
     *
     * @return max type
     */
    Long selectMaxType();

}
