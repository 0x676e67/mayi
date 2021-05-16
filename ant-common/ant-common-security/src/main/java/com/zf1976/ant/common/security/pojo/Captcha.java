package com.zf1976.ant.common.security.pojo;


/**
 * @author mac
 * Create by Ant on 2020/9/1 下午11:07
 */
public class Captcha {

    /**
     * 返回前端的uuid
     */
    private final String uuid;
    /**
     * 返回前端的二维码
     */
    private final String img;

    public Captcha(String uuid, String img) {
        this.uuid = uuid;
        this.img = img;
    }

    public String getUuid() {
        return uuid;
    }

    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "Captcha{" +
                "uuid='" + uuid + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
