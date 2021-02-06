package com.zf1976.ant.common.monitor.pojo;

import lombok.Data;

/**
 * @author mac
 * @date 2021/1/1
 **/
@Data
public class SystemInfoVo {

    private OperatingSystemVO operatingSystem;

    private CpuVO cpu;

    private MemoryVO memory;

    private SwapVO swap;

    private DiskVO disk;

    private String datetime;
}
