package com.zf1976.ant.common.cache;

/**
 * @author mac
 * @date 2021/2/4
 **/
public enum StandardMode {

    /**
     * 单例模式
     */
    SINGLE,

    /**
     * 哨兵模式
     */
    SENTINEL,

    /**
     * 集群模式
     */
    CLUSTER
}
