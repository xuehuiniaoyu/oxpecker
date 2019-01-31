package org.ny.woods.interceptor;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.dimens.HDimens;
import org.ny.woods.interceptor.i.HInterceptor;
import org.ny.woods.layout.widget.HRelativeLayout;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.utils.Reflect;

import java.util.HashMap;
import java.util.Iterator;

public class BodyInterceptor implements HInterceptor<HView<? extends View>> {

    /**
     *
     * 自定义静态申明，可重用的代码块
     */
    private final HashMap<String, StaticInfo> user_static = new HashMap<>();

    /**
     *
     * 自定义别名
     */
    private final HashMap<String, String> user_define = new HashMap<>();

    /**
     * 上下文
     */
    private Context context;

    /**
     *
     * 尺寸工具
     */
    private HDimens dimens;

    /**
     *
     * 静态申明对象
     */
    private static final class StaticInfo {
        /**
         *
         * 为后面代码使用的名称
         */
        String name;

        /**
         *
         * 指向HView的包名
         */
        String tagName;

        /**
         *
         * 预先设置的属性集
         */
        JsonObject value;
    }

    @Override
    public HView<? extends View> onInterceptor(Context context, HDimens dimens, JsonObject jsonObject) {
        this.context = context;
        this.dimens = dimens;
        // 获取屏幕尺寸
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        this.dimens.displayMetrics = displayMetrics;
        JsonValue define = jsonObject.get("define");
        if(define != null) {
            Iterator<JsonObject.Member> iterator = define.asObject().iterator();
            while (iterator.hasNext()) {
                JsonObject.Member member = iterator.next();
                if(member.getName().equals("static")) {
                    Iterator<JsonObject.Member> i = member.getValue().asObject().iterator();
                    while(i.hasNext()) {
                        JsonObject.Member static_M = i.next();
                        JsonObject defineObj = static_M.getValue().asObject();
                        StaticInfo staticInfo = new StaticInfo();
                        staticInfo.name = static_M.getName();
                        staticInfo.value = defineObj.get("attrs").asObject();
                        staticInfo.tagName = defineObj.getString("tag", null);
                        user_static.put(staticInfo.name, staticInfo);
                    }
                }
                else {
                    user_define.put(member.getName(), member.getValue().asString());
                }
            }
        }
        JsonObject body = jsonObject.get("body").asObject();
        HRelativeLayout bodyLayout = new HRelativeLayout(context, body);
        final HView<? extends View> layoutHNode = recursionChildren(bodyLayout);
        layoutHNode.getView().post(new Runnable() {
            @Override
            public void run() {
                layoutHNode.onMeasure(layoutHNode.getView().getWidth(), layoutHNode.getView().getHeight());
            }
        });
        return layoutHNode;
    }

    Reflect reflect = new Reflect();
    HView<? extends View> recursionChildren(HView<? extends View> parent) {
        parent.dimens(dimens);
        JsonValue jsonValue = parent.getValue();
        Iterator<JsonObject.Member> iterator = jsonValue.asObject().iterator();
        while (iterator.hasNext()) {
            JsonObject.Member member = iterator.next();
            if(member.getValue().isObject()) {
                try {
                    HView<? extends View> node = reflect.clear()
                            .on(getTagName(member.getName()), context.getClassLoader())
                            .constructor(Context.class, JsonValue.class)
                            .newInstance(context, member.getValue());
                    node.dimens(dimens.newHDimens());
                    staticInit(member.getName(), node);
                    HView<? extends View> child = recursionChildren(node);
                    if (child != null) {
                        parent.addChild(child);
                    }
                } catch (NoClassDefFoundError e) {
                    parent.setAttr(member.getName(), member.getValue());
                    //throw new HLayoutException("Unknown view type of '"+member.getName()+"' is not "+HView.class.getName());
                }
            }
            else {
                parent.setAttr(member.getName(), member.getValue());
            }
        }
        parent.onLayout();
        return parent;
    }

    String getTagName(String name) {
        if(user_static.containsKey(name))
            return user_static.get(name).tagName;
        if(user_define.containsKey(name))
            return user_define.get(name);
        return name;
    }

    /**
     * 初始化静态属性
     * @param name
     * @param hView
     */
    void staticInit(String name, HView<?> hView) {
        if(user_static.containsKey(name)) {
            StaticInfo staticInfo = user_static.get(name);
            Iterator<JsonObject.Member> iterator = staticInfo.value.iterator();
            while (iterator.hasNext()) {
                JsonObject.Member member = iterator.next();
                hView.setAttr(member.getName(), member.getValue());
            }
        }
    }
}
