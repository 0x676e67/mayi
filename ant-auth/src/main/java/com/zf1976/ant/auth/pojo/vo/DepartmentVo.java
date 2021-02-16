package com.zf1976.ant.auth.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:02 下午
 */
@Data
public class DepartmentVo implements Serializable {

    /**
     * 部门id
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;
}
