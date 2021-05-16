package com.zf1976.ant.upms.biz.pojo.query;


/**
 * @author mac
 * @date 2020/10/23 7:17 下午
 */
public class DictDetailQueryParam extends AbstractQueryParam {

    /**
     * like
     */
    private String dictName;

    /**
     * like
     */
    private String label;

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "DictDetailQueryParam{" +
                "dictName='" + dictName + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
