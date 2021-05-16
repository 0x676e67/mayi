package com.zf1976.ant.upms.biz.pojo.vo.job;


import java.util.Date;

/**
 * @author mac
 * @date 2020/10/26 5:12 下午
 */
public class PositionExcelVO {

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 岗位状态
     */
    private Boolean enabled;

    /**
     * 创建者
     */
    private String createBy;


    /**
     * 创建日期
     */
    private Date createTime;

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
        return "PositionExcelVO{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
