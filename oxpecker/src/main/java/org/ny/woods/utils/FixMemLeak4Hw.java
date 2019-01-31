package org.ny.woods.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 *
 * 华为手机有部分Android系统存在内存泄露
 * 如果你的项目集成了leakcanary 就会频繁的报出错误
 *
 * 也许这个抛出并不是个泄露，若是真正的泄露相信华为公司早会修复了，该泄露一直从6.x延续到8.x依然存在，
 * 只会出现在频繁切换页面时
 *
 */
public class FixMemLeak4Hw {
    private static Field field;
    private static boolean hasField = true;

    @SuppressLint("NewApi")
    public static void fixLeak(Context context) {
        if (!hasField) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mLastSrvView"};
        for (String param : arr) {
            try {
                if (field == null) {
                    field = imm.getClass().getDeclaredField(param);
                }
                if (field == null) {
                    hasField = false;
                }
                if (field != null) {
                    field.setAccessible(true);
                    field.set(imm, null);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
