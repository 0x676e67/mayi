package com.zf1976.ant.upms.biz.pojo.dto.dict;

import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author mac
 * @date 2020/10/23 3:05 下午
 */
@Data
public class DictDTO {

    /**
     * id
     */
    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * 字典名称
     */
    @NotBlank
    private String dictName;

    /**
     * 描述
     */
    @NotBlank
    private String description;

}
