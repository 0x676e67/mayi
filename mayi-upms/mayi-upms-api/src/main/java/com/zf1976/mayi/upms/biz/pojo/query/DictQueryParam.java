package com.zf1976.mayi.upms.biz.pojo.query;

import com.zf1976.mayi.upms.biz.pojo.query.annotation.Param;
import com.zf1976.mayi.upms.biz.pojo.query.enmus.Type;

/**
 * @author mac
 * @date 2020/10/23 7:15 下午
 */
public class DictQueryParam extends AbstractQueryParam {

    /**
     * like
     */
    @Param(type = Type.LIKE, fields = {"dictName"})
    private String blurry;

    public String getBlurry() {
        return blurry;
    }

    public void setBlurry(String blurry) {
        this.blurry = blurry;
    }

    @Override
    public String toString() {
        return "DictQueryParam{" +
                "blurry='" + blurry + '\'' +
                '}';
    }
}
