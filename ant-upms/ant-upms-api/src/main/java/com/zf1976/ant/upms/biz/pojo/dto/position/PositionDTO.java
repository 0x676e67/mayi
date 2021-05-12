package com.zf1976.ant.upms.biz.pojo.dto.position;

import com.zf1976.ant.common.core.validate.ValidationInsertGroup;
import com.zf1976.ant.common.core.validate.ValidationUpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author mac
 * @date 2020/10/25 5:08 下午
 */
@Data
public class PositionDTO {

    /**
     * id
     */
    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * 岗位名称
     */
    @NotBlank
    private String name;

    /**
     * 岗位状态
     */
    @NotNull
    private Boolean enabled;

    /**
     * 排序
     */
    @NotNull
    private Integer jobSort;

}
