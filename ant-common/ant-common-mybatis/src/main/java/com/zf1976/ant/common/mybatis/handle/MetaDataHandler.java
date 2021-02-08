package com.zf1976.ant.common.mybatis.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

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
        Object principal = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getPrincipal();
        return principal.toString();
    }

}
