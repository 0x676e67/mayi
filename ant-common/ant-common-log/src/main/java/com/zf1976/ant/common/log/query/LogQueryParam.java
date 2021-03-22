package com.zf1976.ant.common.log.query;

import com.zf1976.ant.common.log.pojo.enums.LogType;
import com.zf1976.ant.upms.biz.pojo.query.AbstractQueryParam;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2021/1/26
 **/
@Data
public class LogQueryParam extends AbstractQueryParam {

    /**
     * 模糊搜索
     */
    private String blurry;

    /**
     * 创建时间
     */
    private List<Date> createTime;

    /**
     * 日志类型查询参数
     */
    LogType logType;

}
