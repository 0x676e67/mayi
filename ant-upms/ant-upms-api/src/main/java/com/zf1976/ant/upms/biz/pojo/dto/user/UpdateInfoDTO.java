package com.zf1976.ant.upms.biz.pojo.dto.user;

import com.zf1976.ant.upms.biz.pojo.enums.GenderEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author mac
 * @date 2020/10/22 12:51 下午
 */
public class UpdateInfoDTO {

    /**
     * 用户id
     */
    @NotNull
    private Long id;

    /**
     * 性别
     */
    @NotNull
    private GenderEnum gender;

    /**
     * 昵称
     */
    @NotBlank
    private String nickName;

    /**
     * 手机号
     */
    @NotBlank
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UpdateInfoDTO{" +
                "id=" + id +
                ", gender=" + gender +
                ", nickName='" + nickName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
