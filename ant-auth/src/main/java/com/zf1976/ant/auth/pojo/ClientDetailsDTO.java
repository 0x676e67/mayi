package com.zf1976.ant.auth.pojo;


import com.power.common.util.RandomUtil;

import java.util.Random;

/**
 * @author mac
 * @date 2021/4/26
 */
public class ClientDetailsDTO {

    /**
     * 客户端所能访问的资源id集合，多个资源时用逗号(,)分隔
     */
    private String resourceIds;

    /**
     * 客户端访问密匙
     */
    private String clientSecret;

    /**
     * 客户端申请的权限范围，可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔
     */
    private String scope;

    /**
     * authorization_code,password,refresh_token,implicit,client_credentials
     */
    private String authorizedGrantTypes;

    /**
     * 客户端重定向URI，当grant_type为authorization_code或implicit时, 在Oauth的流程中会使用并检查与数据库内的redirect_uri是否一致
     */
    private String webServerRedirectUri;

    /**
     * 客户端所拥有的Spring Security的权限值,可选, 若有多个权限值,用逗号(,)分隔
     */
    private String authorities;

    /**
     * 设定客户端的access_token的有效时间值(单位:秒)，若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时)
     */
    private Integer accessTokenValidity;

    /**
     * 设定客户端的refresh_token的有效时间值(单位:秒)，若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天)
     */
    private Integer refreshTokenValidity;

    /**
     * 这是一个预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据
     */
    private String additionalInformation;

    /**
     * 设置用户是否自动批准授予权限操作, 默认值为 ‘false’, 可选值包括 ‘true’,‘false’, ‘read’,‘write’.
     */
    private String autoApprove;

    public ClientDetailsDTO() {

    }
}
