package com.zf1976.ant.upms.biz.config.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zf1976.ant.common.security.support.session.SessionManagement;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 自动填充数据拦截器
 *
 * @author mac
 * @date 2021/2/8
 **/
public class MetaDataHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", this.getPrincipal(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", this.getPrincipal(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        final Object updateTime = getFieldValByName("updateTime", metaObject);
        if (updateTime != null) {
            this.setFieldValByName("updateTime", new Date(), metaObject);
        }
        final Object updateBy = getFieldValByName("updateBy", metaObject);
        if (updateBy != null) {
            this.setFieldValByName("updateBy", this.getPrincipal(), metaObject);
        }
    }

    private String getPrincipal() {
        return SessionManagement.getUsername();
    }

}
