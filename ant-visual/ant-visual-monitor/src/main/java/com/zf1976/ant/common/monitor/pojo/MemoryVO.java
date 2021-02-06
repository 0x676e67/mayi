package com.zf1976.ant.common.monitor.pojo;

import lombok.Data;

/**
 * @author mac
 * @date 2021/1/1
 **/
@Data
public class MemoryVO {

    /**
     * 总的
     */
    private String total;

    /**
     * 空闲
     */
    private String available;

    /**
     * 使用
     */
    private String used;

    /**
     *  使用率
     */
    private String usageRate;

}
