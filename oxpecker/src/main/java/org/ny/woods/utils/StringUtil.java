package org.ny.woods.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static float getNumberFromString(String str) {
        Pattern pattern = Pattern.compile("[^0-9|^.]");
        Matcher matcher = pattern.matcher(str);
        return Float.valueOf(matcher.replaceAll(""));
    }

    /**
     * 过滤字符
     * @param str
     * @return
     */
    public static String matchString(String str) {
        Pattern pattern = Pattern.compile("[0-9|.]");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("").trim();
    }

    public static void main(String[] args) {
        System.out.println(getNumberFromString("lsjdklfsj100. 12 f"));
    }
}
