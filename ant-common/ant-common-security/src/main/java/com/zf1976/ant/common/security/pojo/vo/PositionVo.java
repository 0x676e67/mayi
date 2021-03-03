package com.zf1976.ant.common.security.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:08 下午
 */
@Data
public class PositionVo implements Serializable {

    /**
     * 岗位id
     */
    private Long id;

    /**
     * 岗位名称
     */
    private String name;

}
