package com.zf1976.ant.upms.biz.pojo.vo.role;

import com.zf1976.ant.upms.biz.pojo.enums.DataPermissionEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/21
 **/
@Data
public class RoleVO {

    /**
     * id
     */
    private Long id;

    /**
     * 角色所有部门
     */
    private List<Long> departmentIds;

    /**
     * 角色所有菜单id
     */
    private Set<Long> menuIds;

    /**
     * 名称
     */
    private String name;

    /**
     * 角色级别
     */
    private Integer level;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 数据权限
     */
    private DataPermissionEnum dataScope;

    /**
     * 创建日期
     */
    private Date createTime;

}
