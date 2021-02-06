package com.zf1976.ant.upms.biz.pojo.query;

import com.zf1976.ant.upms.biz.pojo.query.annotation.Param;
import com.zf1976.ant.common.core.foundation.query.AbstractQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.enmus.Type;
import lombok.Data;

/**
 * @author mac
 * @date 2020/10/23 7:15 下午
 */
@Data
public class DictQueryParam extends AbstractQueryParam {

    /**
     * like
     */
    @Param(type = Type.LIKE, fields = {"dictName"})
    private String blurry;

}
