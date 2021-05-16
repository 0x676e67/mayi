package com.zf1976.ant.auth.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

/**
 * @author mac
 * @date 2020/12/25
 **/
@TableName("sys_resource")
public class SysResource extends Model<SysResource> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * pid resource
     */
    private Long pid;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源url
     */
    private String uri;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 开关
     */
    private Boolean enabled;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 放行
     */
    private Boolean allow;

    /**
     * 叶子
     */
    private Boolean leaf;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新着
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 版本号
     */
    @Version
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAllow() {
        return allow;
    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SysResource{" +
                "id=" + id +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", method='" + method + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                ", allow=" + allow +
                ", leaf=" + leaf +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                '}';
    }
}
