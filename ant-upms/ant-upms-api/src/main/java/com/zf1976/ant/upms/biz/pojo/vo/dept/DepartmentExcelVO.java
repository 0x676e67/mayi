package com.zf1976.ant.upms.biz.pojo.vo.dept;

import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author mac
 */
@Data
public class DepartmentExcelVO {

    /**
     * 上级部门
     */
    private Long pid;

    /**
     * 子部门数目
     */
    private Integer subCount;

    /**
     * 子部门
     */
    private Collection<Map<String, Object>> children;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean enabled;


    /**
     * 创建日期
     */
    private Date createTime;

}
