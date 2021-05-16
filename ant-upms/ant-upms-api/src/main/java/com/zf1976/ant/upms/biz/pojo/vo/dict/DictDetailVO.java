package com.zf1976.ant.upms.biz.pojo.vo.dict;


/**
 * @author ant
 * Create by Ant on 2020/10/24 4:25 下午
 */
public class DictDetailVO {

    /**
     * id
     */
    private Long id;

    /**
     * 字典id
     */
    private Long dictId;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典值
     */
    private String value;

    /**
     * 排序
     */
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
        return "DictDetailVO{" +
                "id=" + id +
                ", dictId=" + dictId +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", dictSort=" + dictSort +
                '}';
    }
}
