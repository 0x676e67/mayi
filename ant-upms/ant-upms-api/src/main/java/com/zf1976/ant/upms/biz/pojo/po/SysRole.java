package com.zf1976.ant.upms.biz.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.zf1976.ant.upms.biz.pojo.enums.DataPermissionEnum;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 角色表(SysRole)实体类
 *
 * @author makejava
 * @since 2020-08-31 11:35:25
 */
@Data
@TableName("sys_role")
public class SysRole extends Model<SysRole> {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(exist = false)
    private Set<Long> departmentIds;

    @TableField(exist = false)
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
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新着
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 版本号
     */
    @Version
    private Integer version;

}
