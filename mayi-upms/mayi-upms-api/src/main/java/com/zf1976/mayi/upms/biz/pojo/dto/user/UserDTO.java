package com.zf1976.mayi.upms.biz.pojo.dto.user;

import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.enums.GenderEnum;

import javax.validation.constraints.*;
import java.util.Set;

/**
 * @author Windows
 */
public class UserDTO {

    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * 用户部门id
     */
    @NotNull
    private Long departmentId;

    /**
     * 用户岗位id
     */
    @NotNull
    private Set<Long> positionIds;

    /**
     * 用户角色id
     */
    @NotNull
    private Set<Long> roleIds;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 昵称
     */
    @NotBlank
    private String nickName;

    /**
     * 性别
     */
    @NotNull
    private GenderEnum gender;

    /**
     * 邮箱
     */
    @Email
    @NotBlank
    private String email;

    /**
     * 手机号
     */
    @NotBlank
    private String phone;

    /**
     * 冻结/开启
     */
    @NotNull
    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Set<Long> getPositionIds() {
        return positionIds;
    }

    public void setPositionIds(Set<Long> positionIds) {
        this.positionIds = positionIds;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", departmentId=" + departmentId +
                ", positionIds=" + positionIds +
                ", roleIds=" + roleIds +
                ", username='" + username + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
