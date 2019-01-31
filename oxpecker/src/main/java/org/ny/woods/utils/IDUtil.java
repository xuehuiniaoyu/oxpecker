package org.ny.woods.utils;

/**
 *
 * 字符串id工具
 * 可将字符串转成int值作为View的ID使用
 */
public class IDUtil {
    private static IDUtil idUtil;
    private IDUtil() {}
    public static IDUtil getIdUtil() {
        if(idUtil == null)
            idUtil = new IDUtil();
        return idUtil;
    }

    public static final int id(String id) {
        return Math.abs(id.hashCode());
    }
}
