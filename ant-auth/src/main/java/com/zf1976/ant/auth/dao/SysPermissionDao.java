package com.zf1976.ant.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.ant.auth.pojo.po.SysPermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * @return /
     */
    List<String> selectListByResourceId(@Param("resourceId") long resourceId);

}
