package com.zf1976.ant.common.security.pojo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:07
 */
@Data
@Builder
public class CaptchaVo {

    /**
     * 返回前端的uuid
     */
    private String uuid;

    /**
     * 返回前端的二维码
     */
    private String img;

}
