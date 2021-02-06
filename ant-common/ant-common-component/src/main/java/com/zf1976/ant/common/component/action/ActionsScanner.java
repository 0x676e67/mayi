package com.zf1976.ant.common.component.action;

import com.zf1976.ant.common.core.util.SpringContextHolder;
import org.springframework.context.ResourceLoaderAware;

import java.util.Map;

/**
 * @author mac
 * @date 2021/1/4
 **/
public interface ActionsScanner extends ResourceLoaderAware {

    ActionsScanner INSTANCE = SpringContextHolder.getBean(SystemActionsScanner.class);

    /**
     * 包扫描
     *
     * @param basePackage package
     * @return /
     */
    Map<Class<?>, String> doScan(String basePackage);
}
