package com.zf1976.ant.upms.biz.pojo.vo.dept;


import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author mac
 */
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

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public Collection<Map<String, Object>> getChildren() {
        return children;
    }

    public void setChildren(Collection<Map<String, Object>> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DepartmentExcelVO{" +
                "pid=" + pid +
                ", subCount=" + subCount +
                ", children=" + children +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}
