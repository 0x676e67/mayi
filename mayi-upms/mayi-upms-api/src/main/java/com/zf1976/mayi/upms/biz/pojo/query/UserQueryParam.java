package com.zf1976.mayi.upms.biz.pojo.query;

import com.zf1976.mayi.upms.biz.pojo.query.annotation.Param;
import com.zf1976.mayi.upms.biz.pojo.query.enmus.Type;

import java.util.Date;
import java.util.List;

/**
 * @author Windows
 */
public class UserQueryParam extends AbstractQueryParam {

    @Param(type = Type.LIKE, fields = {"username", "nickName", "gender"})
    private String blurry;

    @Param(type = Type.EQ)
    private Boolean enabled;

    @Param(type = Type.BETWEEN)
    private List<Date> createTime;

    private Long departmentId;

    public String getBlurry() {
        return blurry;
    }

    public void setBlurry(String blurry) {
        this.blurry = blurry;
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "UserQueryParam{" +
                "blurry='" + blurry + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", departmentId=" + departmentId +
                '}';
    }
}
