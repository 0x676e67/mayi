package com.zf1976.mayi.upms.biz.security;

import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 容器启动后执行一些任务
 *
 * @author mac
 * @date 2021/5/18
 */
@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    private final DynamicDataSourceService dynamicDataSourceService;

    public ApplicationStartupRunner(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

    @Override
    public void run(String... args) {
        // 动态数据源鉴权初始化资源
        this.dynamicDataSourceService.loadDynamicDataSource();
        this.dynamicDataSourceService.loadAllowUri();
    }

}
