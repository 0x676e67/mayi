package com.zf1976.ant.common.cache.dev;

import com.zf1976.ant.common.cache.StandardMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 * @date 2021/2/5
 **/
@Data
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
}
