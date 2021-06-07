package com.zf1976.mayi.upms.biz.service.util;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2020/12/24
 **/
public final class LambdaMethodUtils {

    private LambdaMethodUtils(){

    }

    @SafeVarargs
    public static <T> String columnsToString(SFunction<T, ?>... columns) {
        return columnsToString(true, columns);
    }

    @SafeVarargs
    public static <T> String columnsToString(boolean onlyColumn, SFunction<T, ?>... columns) {
        return Arrays.stream(columns)
                     .map((i) -> columnToString(i, onlyColumn))
                     .collect(Collectors.joining(","));
    }

    public static <T> String columnToString(SFunction<T, ?> column) {
        return columnToString(column, true);
    }

    public static <T> String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        return getColumn(LambdaUtils.resolve(column), onlyColumn);
    }

    private static <T> String getColumn(SerializedLambda lambda, boolean onlyColumn) {
        return PropertyNameUtils.methodToProperty(lambda.getImplMethodName());
    }

}
