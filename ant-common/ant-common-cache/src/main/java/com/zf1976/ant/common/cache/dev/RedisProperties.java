package com.zf1976.ant.common.cache.dev;

import com.zf1976.ant.common.cache.StandardMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/2/5
 **/
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    /**
     * 模式： single-单点，cluster-集群，sentinel-主从
     */
    private StandardMode mode;

    /**
     * 地址配置，根据模式进行不同的配置：
     * single - 单个ip
     * sentinel / cluster - 英文逗号分隔的多个ip:端口，如：127.0.0.1:63791,127.0.0.1:63792
     */
    private String host = "127.0.0.1";

    /**
     * sentinel模式参数 - masterName
     */
    private String masterName;

    /**
     * single模式参数 - 端口
     */
    private Integer port = 6379;

    /**
     * 数据库
     */
    private Integer database = 0;

    /**
     * 密码
     */
    private String password;

    /**
     * 读取超时时间(毫秒)
     */
    private Integer timeOut = 5000;

    /**
     * 最大连接数
     */
    private Integer maxTotal = 200;

    /**
     * 最大空闲连接数
     */
    private Integer maxIdle = 8;

    /**
     * 最小空闲连接数
     */
    private Integer minIdle = 0;

    /**
     * 获取连接时的最大等待毫秒数
     */
    private Long maxWaitMillis = -1L;

    public StandardMode getMode() {
        return mode;
    }

    public void setMode(StandardMode mode) {
        this.mode = mode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }
}
