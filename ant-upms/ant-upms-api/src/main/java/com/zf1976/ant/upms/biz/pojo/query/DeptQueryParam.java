package com.zf1976.ant.upms.biz.pojo.query;

import com.zf1976.ant.upms.biz.pojo.query.annotation.Param;
import com.zf1976.ant.upms.biz.pojo.query.enmus.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/10/26 6:07 下午
 */
@Data
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

}
