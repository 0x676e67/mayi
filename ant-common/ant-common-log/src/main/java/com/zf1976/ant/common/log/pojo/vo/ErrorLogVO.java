package com.zf1976.ant.common.log.pojo.vo;

import com.zf1976.ant.common.log.pojo.enums.LogType;
import com.zf1976.ant.common.log.pojo.vo.base.AbstractLogVO;
import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2021/1/26
 **/
@Data
public class ErrorLogVO extends AbstractLogVO {

    private Long id;

    /**
     * 日志类型
     */
    private LogType logType;

    /**
     * 操作用户
     */
    private String username;

    /**
     * IP地址
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipRegion;

    /**
     * URI
     */
    private String uri;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private Object parameter;

    /**
     * 描述
     */
    private String description;

    /**
     * user agent
     */
    private String userAgent;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 异常细节
     */
    private String exceptionDetails;

    /**
     * 消耗时间 /ms
     */
    private Integer spendTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
