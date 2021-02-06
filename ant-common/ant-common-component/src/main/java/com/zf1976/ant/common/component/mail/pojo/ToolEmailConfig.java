package com.zf1976.ant.common.component.mail.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 邮箱配置(ToolEmailConfig)实体类
 *
 * @author makejava
 * @since 2020-10-17 00:08:54
 */
@Data
public class ToolEmailConfig extends Model<ToolEmailConfig> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发件人
     */
    private String fromUser;

    /**
     * 邮件服务器SMTP地址
     */
    private String host;

    /**
     * 密码
     */
    private String pass;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 发件者用户名
     */
    private String user;

}
