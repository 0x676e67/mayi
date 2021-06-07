package com.zf1976.mayi.common.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具
 *
 * @author mac
 * @date 2021/2/4
 **/
public class ExceptionUtils {

    public static String getStackTraceAsString(Throwable throwable) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            StringBuffer exceptionBuffer = stringWriter.getBuffer();
            return exceptionBuffer.toString();
        } catch (Exception e) {
            return "get stack information exception";
        }
    }
}
