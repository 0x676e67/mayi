package com.zf1976.mayi.upms.biz.pojo;

/**
 * @author mac
 * @date 2021/5/27
 */
public class BindingPermission {

    /**
     * 绑定权限id
     */
    private Long id;

    /**
     * 权限值
     */
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BindingPermission{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
