package com.zf1976.ant.upms.biz.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2021/1/23
 **/
@Data
public class SessionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * IP
     */
    private String ip;

    /**
     * 地址
     */
    private String ipRegion;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 登录时间
     */
    private Date loginTime;
}
