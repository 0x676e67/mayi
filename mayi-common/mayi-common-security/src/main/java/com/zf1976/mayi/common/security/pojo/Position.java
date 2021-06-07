package com.zf1976.mayi.common.security.pojo;


import java.io.Serializable;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:08 下午
 */
public class Position implements Serializable {

    /**
     * 岗位id
     */
    private Long id;

    /**
     * 岗位名称
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
        return "Position{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
