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
    private String url;

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
