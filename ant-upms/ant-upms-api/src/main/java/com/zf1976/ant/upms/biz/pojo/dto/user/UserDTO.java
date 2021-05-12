package com.zf1976.ant.upms.biz.pojo.dto.user;

import com.zf1976.ant.common.core.validate.ValidationInsertGroup;
import com.zf1976.ant.common.core.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.enums.GenderEnum;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Set;

/**
 * @author Windows
 */
@Data
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

}
