package com.zf1976.mayi.upms.biz.pojo.vo.user;


import com.zf1976.mayi.upms.biz.pojo.enums.GenderEnum;
import com.zf1976.mayi.upms.biz.pojo.vo.dept.DepartmentVO;

import java.util.Date;

/**
 * @author Windows
 */
public class UserVO {

    /**
     * id
     */
    private Long id;

    /**
     * 部门
     */
    private DepartmentVO department;

    /**
     * 头像
     */
    private String avatarName;

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
     * 状态：1启用、0禁用
     */
    private Boolean enabled;

    /**
     * 创建日期
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DepartmentVO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentVO department) {
        this.department = department;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", department=" + department +
                ", avatarName='" + avatarName + '\'' +
                ", username='" + username + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}

