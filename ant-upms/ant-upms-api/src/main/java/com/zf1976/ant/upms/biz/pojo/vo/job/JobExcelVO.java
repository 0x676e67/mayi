package com.zf1976.ant.upms.biz.pojo.vo.job;

import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2020/10/26 5:12 下午
 */
@Data
public class JobExcelVO {

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 岗位状态
     */
    private Boolean enabled;

    /**
     * 创建者
     */
    private String createBy;


    /**
     * 创建日期
     */
    private Date createTime;

}
