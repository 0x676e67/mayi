package com.zf1976.ant.common.log.pojo.vo;

import com.zf1976.ant.common.log.pojo.vo.base.AbstractLogVO;
import lombok.Data;

import java.util.Date;

/**
 * @author mac
 * @date 2021/2/2
 **/
@Data
public class UserLogVO extends AbstractLogVO {

    /**
     * 描述
     */
    private String description;

    /**
     * ip
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipRegion;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 消耗时间 /ms
     */
    private Integer spendTime;

    /**
     * 创建时间
     */
    private Date createTime;

}
