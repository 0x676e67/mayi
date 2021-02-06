package com.zf1976.ant.upms.biz.pojo.vo.dept;

import lombok.Data;

import java.util.Collection;
import java.util.Date;

/**
 * @author mac
 * @date 2020/10/26 6:13 下午
 */
@Data
public class DepartmentVO {

    /**
     * id
     */
    private Long id;

    /**
     * 上级部门
     */
    private Long pid;

    /**
     * 子部门
     */
    private Collection<DepartmentVO> children;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer deptSort;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 是否有下一级
     */
    private Boolean hasChildren;

    /**
     * 叶子
     */
    private Boolean leaf;

    /**
     * 创建日期
     */
    private Date createTime;

}
