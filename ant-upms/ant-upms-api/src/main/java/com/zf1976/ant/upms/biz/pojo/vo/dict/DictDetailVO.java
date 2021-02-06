package com.zf1976.ant.upms.biz.pojo.vo.dict;

import lombok.Data;

/**
 * @author ant
 * Create by Ant on 2020/10/24 4:25 下午
 */
@Data
public class DictDetailVO {

    /**
     * id
     */
    private Long id;

    /**
     * 字典id
     */
    private Long dictId;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典值
     */
    private String value;

    /**
     * 排序
     */
    private Integer dictSort;

}
