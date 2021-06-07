package com.zf1976.mayi.auth.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/5/20
 */
public abstract class AbstractSecurityService<D extends BaseMapper<E>, E> extends ServiceImpl<D, E> {

    /**
     * 分页对象类型转换
     *
     * @param sourcePage 源分页对象
     * @param translator 翻译
     * @return {@link IPage < PermissionVO >}
     * @date 2021-05-12 09:37:44
     */
    protected <T> IPage<T> mapToTarget(IPage<E> sourcePage, Function<E, T> translator) {
        List<T> targetPageList = sourcePage.getRecords()
                                           .stream()
                                           .map(translator)
                                           .collect(Collectors.toList());
        return new Page<T>(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount()).setRecords(targetPageList);
    }
}
