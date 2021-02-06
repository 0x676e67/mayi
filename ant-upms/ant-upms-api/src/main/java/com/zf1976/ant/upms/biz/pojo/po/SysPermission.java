package com.zf1976.ant.upms.biz.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2020/12/25
 **/
@Data
@TableName("sys_permission")
public class SysPermission extends Model<SysPermission> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父节点id
     */
    private Long pid;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限值
     */
    private String value;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号
     */
    @Version
    private Integer version;
}
