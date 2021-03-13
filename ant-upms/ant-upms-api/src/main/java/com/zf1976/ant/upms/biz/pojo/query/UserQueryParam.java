package com.zf1976.ant.upms.biz.pojo.query;

import com.zf1976.ant.upms.biz.pojo.query.annotation.Param;
import com.zf1976.ant.upms.biz.pojo.query.enmus.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Windows
 */
@Data
public class UserQueryParam extends AbstractQueryParam {

    @Param(type = Type.LIKE, fields = {"username","nickName","gender"})
    private String blurry;

    @Param(type = Type.EQ)
    private Boolean enabled;

    @Param(type = Type.BETWEEN)
    private List<Date> createTime;

    private Long departmentId;
}
