package com.zf1976.ant.upms.biz.pojo.dto.user;

import com.zf1976.ant.upms.biz.pojo.enums.GenderEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author mac
 * @date 2020/10/22 12:51 下午
 */
@Data
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

}
