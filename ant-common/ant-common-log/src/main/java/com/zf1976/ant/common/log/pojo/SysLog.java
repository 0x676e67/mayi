package com.zf1976.ant.common.log.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.zf1976.ant.common.log.pojo.enums.LogType;
import java.util.Date;

/**
 * @author mac
 * @date 2020/12/24
 **/
@TableName("sys_log")
public class SysLog extends Model<SysLog> {

    @TableId(type = IdType.AUTO)
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

    public Long getId() {
        return id;
    }

    public SysLog setId(Long id) {
        this.id = id;
        return this;
    }

    public LogType getLogType() {
        return logType;
    }

    public SysLog setLogType(LogType logType) {
        this.logType = logType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SysLog setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public SysLog setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public SysLog setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public SysLog setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public SysLog setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public Object getParameter() {
        return parameter;
    }

    public SysLog setParameter(Object parameter) {
        this.parameter = parameter;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SysLog setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public SysLog setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public SysLog setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public SysLog setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getExceptionDetails() {
        return exceptionDetails;
    }

    public SysLog setExceptionDetails(String exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
        return this;
    }

    public Integer getSpendTime() {
        return spendTime;
    }

    public SysLog setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public SysLog setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "SysLog{" +
                "id=" + id +
                ", logType=" + logType +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", ipRegion='" + ipRegion + '\'' +
                ", uri='" + uri + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", parameter=" + parameter +
                ", description='" + description + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", exceptionDetails='" + exceptionDetails + '\'' +
                ", spendTime=" + spendTime +
                ", createTime=" + createTime +
                '}';
    }
}
