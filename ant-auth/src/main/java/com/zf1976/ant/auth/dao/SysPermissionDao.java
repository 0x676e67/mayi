package com.zf1976.ant.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.ant.auth.pojo.BindingPermission;
import com.zf1976.ant.auth.pojo.po.SysPermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author mac
 * @date 2020/12/25
 **/
@Repository
public interface SysPermissionDao extends BaseMapper<SysPermission> {

    /**
     * 获取资源权限值
     *
     * @param resourceId 资源id
     * @return {@link List<String>}
     */
    List<BindingPermission> selectPermissionsByResourceId(@Param("resourceId") long resourceId);

    /**
     * 获取角色权限值
     *
     * @param roleId 角色id
     * @return {@link List<String>}
     */
    List<BindingPermission> selectPermissionsByRoleId(@Param("roleId") long roleId);

    /**
     * 保存权限与资源关系
     *
     * @param resourceId 资源id
     * @param permissionIdList 权限id列表
     */
    void saveResourceRelation(@Param("resourceId") long resourceId, @Param("permissionIdList") Collection<Long> permissionIdList);

    /**
     * 保存权限与校色关系
     *
     * @param roleId 角色id
     * @param permissionIdList 权限id列表
     */
    void saveRoleRelation(@Param("roleId") long roleId, @Param("permissionIdList") Collection<Long> permissionIdList);

    /**
     * 根据权限id删除 权限-资源关系
     *
     * @param id 权限id
     */
    void deleteResourceRelationById(@Param("id") long id);

    /**
     * 根据权限id删除 权限-角色关系
     *
     * @param id 权限id
     */
    void deleteRoleRelationById(@Param("id") long id);

    /**
     * 根据资源id，权限id集合进行解绑
     *
     * @param resourceId 资源id
     * @param permissionIdList 权限id集合
     */
    void deleteResourceRelationByResourceIdAndPermissionIdList(@Param("resourceId") long resourceId, @Param("permissionIdList") Collection<Long> permissionIdList);

    /**
     * 根据角色id，权限id集合进行解绑
     *
     * @param roleId 角色id
     * @param permissionIdList 权限id集合
     */
    void deleteRoleRelationByRoleIdAndPermissionIdList(@Param("roleId") long roleId, @Param("permissionIdList") Collection<Long> permissionIdList);
}
