package com.zf1976.mayi.upms.biz.mail.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 邮箱配置(ToolEmailConfig)实体类
 *
 * @author makejava
 * @since 2020-10-17 00:08:54
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ToolEmailConfig{" +
                "id=" + id +
                ", fromUser='" + fromUser + '\'' +
                ", host='" + host + '\'' +
                ", pass='" + pass + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                '}';
    }
}
