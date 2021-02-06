package com.zf1976.ant.common.monitor.pojo;

import lombok.Data;

/**
 * @author mac
 * @date 2021/1/1
 **/
@Data
public class CpuVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 物理cpu包数
     */
    private Integer physicalPackage;

    /**
     * 物理核心
     */
    private Integer core;

    /**
     * 逻辑核心
     */
    private Integer logic;

    /**
     * 使用
     */
    private String used;

    /**
     * 空闲
     */
    private String idle;

}
