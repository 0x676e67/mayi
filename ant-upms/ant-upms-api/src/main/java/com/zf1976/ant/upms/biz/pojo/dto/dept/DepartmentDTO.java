package com.zf1976.ant.upms.biz.pojo.dto.dept;

import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author mac
 * @date 2020/10/26 6:12 下午
 */
@Data
public class DepartmentDTO {

    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * 上级部门
     */
    private Long pid;

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 排序
     */
    @NotNull
    private Integer deptSort;

    /**
     * 状态
     */
    @NotNull
    private Boolean enabled;

}
