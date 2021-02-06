package com.zf1976.ant.upms.biz.pojo.query;

import com.zf1976.ant.upms.biz.pojo.query.annotation.Param;
import com.zf1976.ant.common.core.foundation.query.AbstractQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.enmus.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/10/25 5:12 下午
 */
@Data
public class PositionQueryParam extends AbstractQueryParam {

    /**
     * like
     */
    @Param(type = Type.LIKE)
    private String name;

    /**
     *  eq
     */
    @Param(type = Type.EQ)
    private Boolean enabled;

    /**
     * between
     */
    @Param(type = Type.BETWEEN)
    private List<Date> createTime;

}
