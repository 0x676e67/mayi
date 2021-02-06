package com.zf1976.ant.upms.biz.pojo.dto.role;

import com.zf1976.ant.upms.biz.pojo.enums.DataPermissionEnum;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/21
 **/
@Data
public class RoleDTO {

    /**
     * id
     */
    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * role name
     */
    @NotBlank
    private String name;

    /**
     * role level
     */
    @NotNull
    private Integer level;

    /**
     * 状态
     */
    @NotNull
    private Boolean enabled;

    /**
     * data scope description
     */
    @NotNull
    private DataPermissionEnum dataScope;

    /**
     * description
     */
    @NotBlank
    private String description;

    /**
     * department id collection
     */
    @NotNull(groups = ValidationUpdateGroup.class)
    private Set<Long> departmentIds;

    /**
     * menu id collection
     */
    @NotNull(groups = ValidationUpdateGroup.class)
    private Set<Long> menuIds;
}
