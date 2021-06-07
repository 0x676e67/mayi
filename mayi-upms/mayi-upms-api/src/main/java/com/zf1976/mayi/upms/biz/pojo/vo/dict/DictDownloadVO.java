package com.zf1976.mayi.upms.biz.pojo.vo.dict;


import java.util.Date;

/**
 * @author mac
 * @date 2020/10/24 10:01 下午
 */
public class DictDownloadVO {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 描述
     */
    private String description;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典值
     */
    private String value;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建日期
     */
    private Date createTime;


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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DictDownloadVO{" +
                "dictName='" + dictName + '\'' +
                ", description='" + description + '\'' +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
