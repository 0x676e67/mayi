package com.zf1976.ant.upms.biz.pojo.query;

import lombok.Data;

/**
 * @author mac
 * @date 2020/10/23 7:17 下午
 */
@Data
public class DictDetailQueryParam extends AbstractQueryParam {

    /**
     * like
     */
    private String dictName;

    /**
     * like
     */
    private String label;

}
