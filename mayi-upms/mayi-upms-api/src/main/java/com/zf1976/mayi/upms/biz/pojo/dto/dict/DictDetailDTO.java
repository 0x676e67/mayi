package com.zf1976.mayi.upms.biz.pojo.dto.dict;

import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author ant
 * Create by Ant on 2020/10/24 4:25 下午
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getDictSort() {
        return dictSort;
    }

    public void setDictSort(Integer dictSort) {
        this.dictSort = dictSort;
    }

    @Override
    public String toString() {
        return "DictDetailDTO{" +
                "id=" + id +
                ", dictId=" + dictId +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", dictSort=" + dictSort +
                '}';
    }
}
