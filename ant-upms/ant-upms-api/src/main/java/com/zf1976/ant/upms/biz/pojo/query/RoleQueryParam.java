package com.zf1976.ant.upms.biz.pojo.query;

import com.zf1976.ant.upms.biz.pojo.query.annotation.Param;
import com.zf1976.ant.common.core.foundation.query.AbstractQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.enmus.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/11/21
 **/
@Data
public class RoleQueryParam extends AbstractQueryParam {

    @Param(type = Type.LIKE, fields = {"name", "description"})
    private String blurry;

    @Param(type = Type.BETWEEN)
    private List<Date> createTime;
}
