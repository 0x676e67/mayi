package com.zf1976.ant.upms.biz.pojo.dto.dict;

import com.zf1976.ant.common.core.validate.ValidationInsertGroup;
import com.zf1976.ant.common.core.validate.ValidationUpdateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author mac
 * @date 2020/10/23 3:05 下午
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DictDTO{" +
                "id=" + id +
                ", dictName='" + dictName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
