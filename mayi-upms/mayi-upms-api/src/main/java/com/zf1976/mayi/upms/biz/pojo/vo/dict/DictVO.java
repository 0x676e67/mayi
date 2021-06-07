package com.zf1976.mayi.upms.biz.pojo.vo.dict;


/**
 * @author mac
 * @date 2020/10/23 3:00 下午
 */
public class DictVO {

    /**
     * id
     */
    private Long id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 描述
     */
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
        return "DictVO{" +
                "id=" + id +
                ", dictName='" + dictName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
