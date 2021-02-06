package com.zf1976.ant.upms.biz.pojo.vo.job;

import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2020/10/25 5:08 下午
 */
@Data
public class PositionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 岗位状态
     */
    private Boolean enabled;

    /**
     * 排序
     */
    private Integer jobSort;

    /**
     * 创建日期
     */
    private Date createTime;

}
