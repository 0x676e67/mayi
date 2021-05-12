package com.zf1976.ant.auth.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zf1976.ant.common.mybatis.handle.MetaDataHandler;
import org.springframework.context.annotation.Bean;

/**
 * @author mac
 * @date 2021/5/12
 */
public class MybatisPlusConfiguration {

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaDataHandler();
    }
}
