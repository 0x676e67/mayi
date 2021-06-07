package com.zf1976.mayi.upms.biz.pojo.dto.user;

import javax.validation.constraints.NotBlank;

/**
 * @author mac
 */
public class UpdateEmailDTO {

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UpdateEmailDTO{" +
                "password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
