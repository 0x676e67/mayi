package com.zf1976.ant.upms.biz.pojo.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author mac
 */
@Data
public class UpdateEmailDTO {

    @NotBlank
    private String password;

    @NotBlank
    private String email;
}
