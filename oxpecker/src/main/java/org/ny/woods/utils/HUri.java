package org.ny.woods.utils;

public class HUri {
    public static boolean isUri(String uri) {
        if (uri == null)
            return false;
        if (uri.contains("http://") ||
                uri.contains("https://") ||
                uri.contains("file://") ||
                uri.contains("file:///") ||
                uri.contains("android.resource")) {
            return true;
        }
        return false;
    }
}
