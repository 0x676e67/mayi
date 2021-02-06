package com.zf1976.ant.upms.biz.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.zf1976.ant.upms.biz.pojo.enums.GenderEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 系统用户(SysUser)实体类
 *
 * @author makejava
 * @since 2020-08-31 11:35:25
 */
@Data
@TableName(value = "sys_user")
public class SysUser extends Model<SysUser> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户部门
     */
    @TableField(exist = false)
    private SysDepartment department;

    /**
     * 用户角色
     */
    @TableField(exist = false)
    private List<SysRole> roleList;

    /**
     * 用户职位
     */
    @TableField(exist = false)
    private List<SysPosition> positionList;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private GenderEnum gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatarName;

    /**
     * 头像真实路径
     */
    private String avatarPath;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态：1启用、0禁用
     */
    private Boolean enabled;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新着
     */
    private String updateBy;

    /**
     * 修改密码的时间
     */
    private Date pwdResetTime;

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
