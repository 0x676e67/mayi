package com.zf1976.ant.upms.biz.pojo.vo.dict;

import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2020/10/24 10:01 下午
 */
@Data
public class DictDownloadVO {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 描述
     */
    private String description;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典值
     */
    private String value;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建日期
     */
    private Date createTime;

}
