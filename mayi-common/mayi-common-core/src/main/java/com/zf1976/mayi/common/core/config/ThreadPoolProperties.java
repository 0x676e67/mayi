package com.zf1976.mayi.common.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 公用线程池配置
 *
 * @author mac
 * @date 2021/5/22
 */
@Component
@ConfigurationProperties(prefix = "executor")
public class ThreadPoolProperties {

    /**
     * 默认线程池大小
     */
    private Integer corePoolSize = Runtime.getRuntime()
                                          .availableProcessors();

    /**
     * 默认最大线程数
     */
    private Integer maxPoolSize = Runtime.getRuntime()
                                         .availableProcessors() << 2;

    /**
     * 默认队列大小
     */
    private Integer queueCapacity = 100;

    /**
     * 默认前缀
     */
    private String namePrefix = "Core-task-";

    /**
     * 线程存活时间
     */
    private Integer KeepAliveSeconds = 200;

    /**
     * 线程组名
     */
    private String threadGroupName = "Default-Group";

    /**
     * Bean名
     */
    private String beanName = "ThreadPoolTaskExecutor-Bean";

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getThreadGroupName() {
        return threadGroupName;
    }

    public void setThreadGroupName(String threadGroupName) {
        this.threadGroupName = threadGroupName;
    }

    public Integer getKeepAliveSeconds() {
        return KeepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        KeepAliveSeconds = keepAliveSeconds;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }
}
