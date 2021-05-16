package com.zf1976.ant.upms.biz.pojo.dto.user;

import javax.validation.constraints.NotBlank;

/**
 * @author mac
 */
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

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
}
