package org.ny.woods.js.native_object.color;

import android.content.Context;
import android.graphics.Color;

import org.ny.woods.js.native_object.NativeObjInfo;
import org.ny.woods.utils.Reflect;

public class ColorManager extends NativeObjInfo {
    private Reflect reflect;
    public ColorManager(Context context) {
        super(context);
    }

    public int parseColor(String color) {
        return Color.parseColor(color);
    }

    public int getColor(String name) {
        if(name == null)
            return Color.TRANSPARENT;

        if(name.contains("@color/")) {
            name = name.substring("@color/".length());
        }

        if(reflect == null) {
            reflect = new Reflect();
        }

        int colorId = reflect.clear().on(getContext().getPackageName()+".R$color").get(name);
        if(colorId != -1) {
            return getContext().getResources().getColor(colorId);
        }
        return Color.TRANSPARENT;
    }
}
