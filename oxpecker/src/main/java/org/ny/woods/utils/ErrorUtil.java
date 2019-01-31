package org.ny.woods.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Administrator on 2016/7/6.
 */
public class ErrorUtil {
    public static String ex(Throwable e) {
        if(e == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
