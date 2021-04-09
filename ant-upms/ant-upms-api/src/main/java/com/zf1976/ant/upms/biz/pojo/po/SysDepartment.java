package com.zf1976.ant.upms.biz.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * 部门(SysDept)实体类
 *
 * @author makejava
 * @since 2020-08-31 11:35:24
 */
@Data
@TableName("sys_department")
public class SysDepartment extends Model<SysDepartment> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上级部门
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long pid;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer deptSort;

    /**
     * 启动
     */
    private Boolean enabled;

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
