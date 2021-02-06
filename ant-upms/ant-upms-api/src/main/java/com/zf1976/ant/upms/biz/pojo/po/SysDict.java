package com.zf1976.ant.upms.biz.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * 数据字典(SysDict)实体类
 *
 * @author makejava
 * @since 2020-08-31 11:35:25
 */
@Data
@TableName("sys_dict")
public class SysDict extends Model<SysDict> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 创建日期
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
