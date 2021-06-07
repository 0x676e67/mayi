package com.zf1976.mayi.common.security.pojo;


import java.io.Serializable;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:02 下午
 */
public class Department implements Serializable {

    /**
     * 部门id
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
