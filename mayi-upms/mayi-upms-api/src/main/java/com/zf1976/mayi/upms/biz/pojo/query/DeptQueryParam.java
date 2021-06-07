package com.zf1976.mayi.upms.biz.pojo.query;

import com.zf1976.mayi.upms.biz.pojo.query.annotation.Param;
import com.zf1976.mayi.upms.biz.pojo.query.enmus.Type;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/10/26 6:07 下午
 */
public class DeptQueryParam extends AbstractQueryParam {

    /**
     * 部门名 模糊查询
     */
    @Param(type = Type.LIKE)
    private String name;

    /**
     * 是否开启
     */
    @Param(type = Type.EQ)
    private Boolean enabled;

    /**
     * 创建时间
     */
    @Param(type = Type.BETWEEN)
    private List<Date> createTime;

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

    public List<Date> getCreateTime() {
        return createTime;
    }

    public void setCreateTime(List<Date> createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DeptQueryParam{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}
