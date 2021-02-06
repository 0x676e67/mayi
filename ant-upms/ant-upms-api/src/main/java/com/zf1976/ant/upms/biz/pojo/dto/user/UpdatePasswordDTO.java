package com.zf1976.ant.upms.biz.pojo.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author mac
 */
@Data
public class UpdatePasswordDTO {

    /**
     * 旧密码
     */
    @NotBlank
    private String oldPass;

    /**
     * 新密码
     */
    @NotBlank
    private String newPass;

}
