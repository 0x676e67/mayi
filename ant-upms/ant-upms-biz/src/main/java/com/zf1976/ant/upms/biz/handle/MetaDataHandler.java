package com.zf1976.ant.upms.biz.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zf1976.ant.common.security.support.session.DistributedSessionManager;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.Objects;

/**
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
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", this.getPrincipal(), metaObject);
    }

    private String getPrincipal() {
        return Objects.requireNonNull(DistributedSessionManager.getSession()).getUsername();
    }

}
