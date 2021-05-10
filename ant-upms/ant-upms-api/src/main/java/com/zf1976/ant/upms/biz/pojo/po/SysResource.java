package com.zf1976.ant.upms.biz.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2020/12/25
 **/
@Data
@TableName("sys_resource")
public class SysResource extends Model<SysResource> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * pid resource
     */
    private Long pid;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源url
     */
    private String uri;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 开关
     */
    private Boolean enabled;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 放行
     */
    private Boolean allow;

    /**
     * 叶子
     */
    private Boolean leaf;

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
