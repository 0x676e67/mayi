package com.zf1976.ant.upms.biz.service.util;

import org.apache.ibatis.reflection.ReflectionException;

import java.util.Locale;

/**
 * @author mac
 * @date 2020/12/24
 **/
public final class PropertyNameUtils {

    public static final String IS = "is";
    public static final String GET = "get";
    public static final String SET = "set";

    private PropertyNameUtils() {
    }

    public static String methodToProperty(String name) {
        if (name.startsWith(IS)) {
            name = name.substring(2);
        } else {
            if (!name.startsWith(GET) && !name.startsWith(SET)) {
                throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    public static boolean isProperty(String name) {
        return isGetter(name) || isSetter(name);
    }

    public static boolean isGetter(String name) {
        return name.startsWith(GET) && name.length() > 3 || name.startsWith(IS) && name.length() > 2;
    }

    public static boolean isSetter(String name) {
        return name.startsWith(SET) && name.length() > 3;
    }
}
