package com.zf1976.ant.upms.biz.pojo.dto.dict;

import com.zf1976.ant.upms.biz.pojo.validate.ValidationInsertGroup;
import com.zf1976.ant.upms.biz.pojo.validate.ValidationUpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author ant
 * Create by Ant on 2020/10/24 4:25 下午
 */
@Data
public class DictDetailDTO {

    /**
     * id
     */
    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * 字典id
     */
    @NotNull
    private Long dictId;

    /**
     * 字典标签
     */
    @NotBlank
    private String label;

    /**
     * 字典值
     */
    @NotBlank
    private String value;

    /**
     * 排序
     */
    @NotNull
    private Integer dictSort;

}
