package com.zf1976.ant.common.security.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zf1976.ant.upms.biz.pojo.enums.GenderEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author ant
 * Create by Ant on 2020/9/8 12:32 下午
 */
@Data
@Accessors(chain = true)
public class UserInfoVo implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 部门
     */
    private DepartmentVo department;

    /**
     * 角色信息
     */
    private Set<RoleVo> roleList;

    /**
     * 职位信息
     */
    private Set<PositionVo> positionList;

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
    @JsonIgnore
    private String password;

    /**
     * 状态：1启用、0禁用
     */
    @JsonIgnore
    private Boolean enabled;

    /**
     * 创建日期
     */
    private Date createTime;

}
