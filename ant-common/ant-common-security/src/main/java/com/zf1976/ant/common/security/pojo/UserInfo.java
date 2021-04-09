package com.zf1976.ant.common.security.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zf1976.ant.common.security.pojo.vo.DepartmentVo;
import com.zf1976.ant.common.security.pojo.vo.PositionVo;
import com.zf1976.ant.common.security.pojo.vo.RoleVo;
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
public class UserInfo implements Serializable {

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

    public Long getId() {
        return id;
    }

    public UserInfo setId(Long id) {
        this.id = id;
        return this;
    }

    public DepartmentVo getDepartment() {
        return department;
    }

    public UserInfo setDepartment(DepartmentVo department) {
        this.department = department;
        return this;
    }

    public Set<RoleVo> getRoleList() {
        return roleList;
    }

    public UserInfo setRoleList(Set<RoleVo> roleList) {
        this.roleList = roleList;
        return this;
    }

    public Set<PositionVo> getPositionList() {
        return positionList;
    }

    public UserInfo setPositionList(Set<PositionVo> positionList) {
        this.positionList = positionList;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserInfo setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public UserInfo setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public UserInfo setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInfo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public UserInfo setAvatarName(String avatarName) {
        this.avatarName = avatarName;
        return this;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public UserInfo setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public UserInfo setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public UserInfo setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", department=" + department +
                ", roleList=" + roleList +
                ", positionList=" + positionList +
                ", username='" + username + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", avatarName='" + avatarName + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}
