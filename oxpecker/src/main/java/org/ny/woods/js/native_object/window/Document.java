package org.ny.woods.js.native_object.window;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.ny.woods.js.native_object.NativeObjInfo;
import org.ny.woods.utils.IDUtil;

public class Document extends NativeObjInfo {
    private Window mWindow;
    public Document(Context context) {
        super(context);
        mWindow = new Window(context);
    }

    /**
     *
     * 获取页面 id为 #id的View
     * @param id
     * @return
     */
    public View findViewById(String id) {
        if(getContext() instanceof Activity) {
            View windowView = ((Activity) getContext()).findViewById(IDUtil.id(id));
            return windowView;
        }
        return null;
    }

    /**
     *
     * 获取View 中 id为 #id的View
     * @param parent
     * @param id
     * @return
     */
    public View findViewByIdFrom(View parent, String id) {
        return parent.findViewById(IDUtil.id(id));
    }

    public Window getWindow() {
        return mWindow;
    }
}
