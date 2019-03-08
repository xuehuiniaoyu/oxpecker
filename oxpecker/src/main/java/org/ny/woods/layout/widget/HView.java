package org.ny.woods.layout.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.annotations.Extension;
import org.ny.woods.dimens.HDimens;
import org.ny.woods.exception.HException;
import org.ny.woods.exception.HLayoutException;
import org.ny.woods.js.channel.JsChannel;
import org.ny.woods.layout.HNode;
import org.ny.woods.layout.tools.FunctionExec;
import org.ny.woods.layout.tools.UiFuncExec;
import org.ny.woods.layout.widget.i.ViewPart;
import org.ny.woods.os.Message;
import org.ny.woods.os.MsgHandler;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.HTemplate;
import org.ny.woods.utils.HUri;
import org.ny.woods.utils.IDUtil;
import org.ny.woods.utils.Reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import glide.GlideApp;
import glide.GlideRequest;

public class HView<T extends View> extends HNode implements ViewPart {

    /**
     * Android Native View
     */
    protected final T mView;

    /**
     * View的尺寸管理工具LayoutParams
     */
    protected LayoutParams mViewLp;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 反射工具，用于动态执行代码
     */
    private Reflect reflect;

    /**
     * 尺寸管理工具，用于计算尺寸
     */
    private HDimens dimens;

    /**
     * 布局解析器
     */
    private Oxpecker oxpecker;

    /**
     * 是否绘制完成
     */
    private boolean uiReaded;


    /**
     * 延迟执行工具，等到UI被加载后再执行方法
     */
    protected final List<UiFuncExec> mUiFuncExecList = new ArrayList<UiFuncExec>() {
        @Override
        public boolean add(UiFuncExec o) {
            if (this.contains(o)) {
                this.remove(o);
            }
            return super.add(o);
        }
    };

    protected final List<UiFuncExec> mAfterReadyUiFuncExecList = new ArrayList<UiFuncExec>() {
        @Override
        public boolean add(UiFuncExec o) {
            if (this.contains(o)) {
                this.remove(o);
            }
            return super.add(o);
        }
    };


    /**
     * js 通道，用于与js脚本交互
     */
    private JsChannel jsChannel;

    /**
     * 设置js通道
     *
     * @param jsChannel
     */
    @Override
    public void setJsChannel(JsChannel jsChannel) {
        this.jsChannel = jsChannel;
    }

    @Override
    public JsChannel getJsChannel() {
        return jsChannel;
    }

    /**
     * 设置布局解析器
     *
     * @param oxpecker
     */
    public void setOxpecker(Oxpecker oxpecker) {
        this.oxpecker = oxpecker;
    }

    public Oxpecker getOxpecker() {
        return oxpecker;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public Reflect getReflect() {
        return reflect;
    }

    public HDimens getDimens() {
        return dimens;
    }

    @Override
    public T getView() {
        return mView;
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * 权重总和
         */
        public int widthWeightSum;
        public int heightWeightSum;

        public int widthWeight;
        public int heightWeight;
    }

    T onGetView(String mViewTypeClassName) {
        try {
            Class<?> mViewClass = context.getClassLoader().loadClass(mViewTypeClassName);
            Constructor<?>[] constructors = mViewClass.getConstructors();
            Constructor<?> constructor = null;
            for(int i = 0; i < constructors.length; i++) {
                if(constructor == null) {
                    constructor = constructors[i];
                }
                else if(constructors[i].getParameterTypes().length < constructor.getParameterTypes().length) {
                    constructor = constructors[i];
                }
                if(constructor.getParameterTypes().length == 1)
                    break;
            }
            if(constructor == null) {
                throw new HException("No constructor!");
            }
            switch (constructor.getParameterTypes().length) {
                case 1: {
                    return (T) constructor.newInstance(context);
                }
                case 2: {
                    return (T) constructor.newInstance(context, null);
                }
                case 3: {
                    return (T) constructor.newInstance(context, null, 0);
                }
                case 4: {
                    return (T) constructor.newInstance(context, null, 0, 0);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public HView(Context context, JsonValue value) {
        super(value);
        this.context = context;
        reflect = new Reflect();
        ParameterizedType c = getGenericSuperclass(getClass());
        if (c != null) {
            Type[] types = c.getActualTypeArguments();
            if (types != null && types.length == 1) {
                try {
                    types[0].getTypeName();
                } catch (Throwable t) {
                }
                String mViewTypeClassName = reflect.clear().on(types[0]).get("name");
                mView = onGetView(mViewTypeClassName);
            } else {
                throw new HLayoutException("Do not specify the correct generic types to <T extends View>");
            }
        } else {
            throw new HLayoutException("Do not specify the correct generic types to <T extends View>");
        }
//        mViewLp = reflect.clear().on(mView.getClass().getName()+"$LayoutParams").constructor(int.class, int.class)
//                .newInstance(0, 0);
        mViewLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mView.setLayoutParams(mViewLp);
//        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (dimens.getParentWidth() <= 0 || dimens.getParentHeight() <= 0) {
//                    onMeasure(mViewLp.width > 0 ? mViewLp.width : mView.getWidth(), mViewLp.height > 0 ? mViewLp.height : mView.getHeight());
//                }
//            }
//        });
        mView.post(new Runnable() {
            @Override
            public void run() {
                onUiReady();
            }
        });
    }

    private ParameterizedType getGenericSuperclass(Class clz) {
        Type type = clz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType;
        }
        if (clz.getSuperclass() != null) {
            return getGenericSuperclass(clz.getSuperclass());
        }
        return null;
    }

    /**
     * 通过名称获取资源id
     * drawable/hello
     * raw/name
     *
     * @param name
     * @return
     */
    protected int getResourceId(String name) {
        if(!name.contains("@")) {
            throw new HException(name+" is not a Resource!");
        }
        name = name.substring(1);
        String[] scopeAndName = name.split("/");
        if (scopeAndName.length == 2) {
            String scope = scopeAndName[0];
            if(scope.contains("android:")) {
                int drawable = getReflect().clear().on("android.R$" + scope.substring(8)).get(scopeAndName[1]);
                return drawable;
            }
            else {
                int drawable = getReflect().clear().on(getContext().getPackageName() + ".R$" + scope).get(scopeAndName[1]);
                return drawable;
            }
        }
        return -1;
    }

    /**
     * 指定尺寸工具
     *
     * @param dimens
     */
    public final void dimens(HDimens dimens) {
        this.dimens = dimens;
    }

    @Override
    public void setAttr(String name, JsonValue value) {
        String extensionName = getExtension(name);
        if (extensionName != null) {
            reflect.clear().on(this).method(extensionName, JsonValue.class).invoke(value);
        } else {
            reflect.clear().on(this).method("set" + name.substring(0, 1).toUpperCase() + name.substring(1), JsonValue.class).invoke(value);
        }
    }

    /**
     * 设置id
     * id: xxx
     *
     * @param value
     */
    public void setId(JsonValue value) {
        mView.setId(IDUtil.id("#" + value.asString()));
    }

    /**
     * 设置布局宽度
     * width:100 |
     * width:50%
     *
     * @param value
     */
    public void setWidth(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setWidth", value) {
            @Override
            protected void onExec(JsonValue value) {
                if(mViewLp.widthWeight == 0) {
                    mViewLp.width = (int) dimens.getWidth(value).getSize();
                }
            }
        });
    }

    /**
     * 设置布局高度
     * height:100 |
     * height:50%
     *
     * @param value
     */
    public void setHeight(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setHeight", value) {
            @Override
            protected void onExec(JsonValue value) {
                if(mViewLp.heightWeight == 0) {
                    mViewLp.height = (int) dimens.getHeight(value).getSize();
                }
            }
        });
    }

    /**
     * 设置横向权重
     *
     * @param value
     */
    public void setWidthWeight(JsonValue value) {
        mViewLp.widthWeight = value.asInt();
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setWidthWeight", null) {
            @Override
            protected void onExec(JsonValue value) {
                if (getParent() != null) {
                    HView parent = getParent();
                    if (parent.mViewLp.widthWeightSum > 0) {
                        mViewLp.width = (int) ((mViewLp.widthWeight * 1.0F / parent.mViewLp.widthWeightSum) * dimens.getWidth() * dimens.getWidthScale());
                    }
                }
            }
        });
    }

    /**
     * 设置纵向权重
     *
     * @param value
     */
    public void setHeightWeight(JsonValue value) {
        mViewLp.heightWeight = value.asInt();
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setHeightWeight", null) {
            @Override
            protected void onExec(JsonValue value) {
                if (getParent() != null) {
                    HView parent = getParent();
                    if (parent.mViewLp.heightWeightSum > 0) {
                        mViewLp.height = (int) ((mViewLp.heightWeight * 1.0F / parent.mViewLp.heightWeightSum) * dimens.getHeight() * dimens.getHeightScale());
                    }
                }
            }
        });
    }


    private CustomViewTarget<View, Bitmap> viewBgTarget;
    GlideRequest bgRequest;

    /**
     * 设置背景色
     * backgroundColor: "#cc0000"
     *
     * @param value
     */
    public void setBackgroundColor(JsonValue value) {
        mView.setBackgroundColor(parseColor(value));
    }

    /**
     *
     * 解析颜色
     * @param value
     * @return
     */
    protected int parseColor(JsonValue value) {
        String valueString = value.asString();
        if (valueString.contains("#")) {
            return Color.parseColor(value.asString());
        }
        int colorId = getResourceId(valueString);
        if(colorId == -1) {
            throw new HException(valueString + " is not Color!");
        }
        return context.getResources().getColor(colorId);
    }

    /**
     * 设置背景图或颜色
     *
     * @param value
     */
    public void setBackground(final JsonValue value) {
        String valueString = value.asString();
        if(HUri.isUri(valueString)) {
            viewBgTarget = new CustomViewTarget<View, Bitmap>(mView) {
                @Override
                protected void onResourceCleared(@Nullable Drawable placeholder) {

                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {

                }

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (!isDied()) {
                        final Drawable drawable = new BitmapDrawable(resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mView.setBackground(drawable); //设置背景
                        }
                    }
                }
            };
            bgRequest = GlideApp.with(context).asBitmap().load(valueString).apply(new RequestOptions().centerInside());
            mAfterReadyUiFuncExecList.add(new UiFuncExec<Void>("setBackground", null) {
                @Override
                protected void onExec(Void value) {
                    bgRequest.into(viewBgTarget);
                }
            });
        }
        else if(valueString.contains("#") || valueString.contains("@color/") || valueString.contains("@android:color/")) {
            setBackgroundColor(value);
        }
        else {
            int resourceId = getResourceId(valueString);
            mView.setBackgroundResource(resourceId);
        }
    }

    /**
     * 设置外边距
     * margin: [0, 0, 0, 0]
     *
     * @param value
     */
    public void setMargin(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonArray>("setMargin", value.asArray()) {
            @Override
            protected void onExec(JsonArray array) {
                mViewLp.setMargins((int) dimens.getWidth(array.get(0)).getSize(), (int) dimens.getHeight(array.get(1)).getSize(), (int) dimens.getWidth(array.get(2)).getSize(), (int) dimens.getHeight(array.get(3)).getSize());
            }
        });
    }

    /**
     * 设置内边距
     * padding: [0, 0, 0, 0]
     *
     * @param value
     */
    public void setPadding(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonArray>("setPadding", value.asArray()) {
            @Override
            protected void onExec(JsonArray array) {
                mView.setPadding((int) dimens.getWidth(array.get(0)).getSize(), (int) dimens.getHeight(array.get(1)).getSize(), (int) dimens.getWidth(array.get(2)).getSize(), (int) dimens.getHeight(array.get(3)).getSize());
            }
        });
    }

    /**
     * 设置滚动条显示
     *
     * @param value
     */
    public void setScrollBar(JsonValue value) {
        switch (value.asString()) {
            case "left": {
                mView.setVerticalScrollBarEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
                }
                break;
            }
            case "right": {
                mView.setVerticalScrollBarEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);
                }
                break;
            }
            case "none": {
                mView.setHorizontalScrollBarEnabled(false);
                mView.setVerticalScrollBarEnabled(false);
                break;
            }
        }
    }

    /**
     * 设置在xxx左边
     * toLeftOf: id
     *
     * @param value
     */
    @Extension("leftOf")
    public void setToLeftOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.LEFT_OF, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx右边
     * toRightOf: id
     *
     * @param value
     */
    @Extension("rightOf")
    public void setToRightOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.RIGHT_OF, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx上边
     * toTopOf: id
     *
     * @param value
     */
    @Extension({"topOf", "above"})
    public void setToTopOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ABOVE, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx下边
     * toBottomOf: id
     *
     * @param value
     */
    @Extension({"bottomOf", "below"})
    public void setToBottomOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.BELOW, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx左对齐
     * asLeft: id
     *
     * @param value
     */
    public void setAsLeft(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_LEFT, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx右对齐
     * asRight: id
     *
     * @param value
     */
    public void setAsRight(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_RIGHT, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx上对齐
     * asTop: id
     *
     * @param value
     */
    public void setAsTop(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_TOP, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx下对齐
     * asBottom: id
     *
     * @param value
     */
    public void setAsBottom(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_BOTTOM, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx父容器左对齐
     * asParentLeft:  true | false
     *
     * @param value
     */
    public void setAsParentLeft(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器右对齐
     * asParentRight:  true | false
     *
     * @param value
     */
    public void setAsParentRight(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器上对齐
     * asParentTop:  true | false
     *
     * @param value
     */
    public void setAsParentTop(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器下对齐
     * asParentBottom:  true | false
     *
     * @param value
     */
    public void setAsParentBottom(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器居中
     * centerInParent:  true | false
     *
     * @param value
     */
    public void setCenterInParent(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在父容器纵向居中
     * centerVertical: true | false
     *
     * @param value
     */
    @Extension({"center_v", "centerV"})
    public void setCenterVertical(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在父容器纵向居中
     * centerHorizontal: true | false
     *
     * @param value
     */
    @Extension({"center_h", "centerH"})
    public void setCenterHorizontal(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置相对父容器
     * asParent: left
     *
     * @param value
     */
    public void setAsParent(JsonValue value) {
        String valueString = value.asString();
        for(String v : valueString.split("\\|")) {
            switch (v) {
                case "left":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    break;
                case "right":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    break;
                case "top":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    break;
                case "bottom":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    break;
                case "center_vertical":
                case "center_v":
                case "centerV":
                    mViewLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    break;
                case "center_horizontal":
                case "center_h":
                case "centerH":
                    mViewLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            }
        }
    }

    /**
     * 设置权重总和
     *
     * @param value
     */
    @Extension("widthSum")
    public void setWidthWeightSum(JsonValue value) {
        mViewLp.widthWeightSum = value.asInt();
    }

    /**
     * 设置权重占比
     *
     * @param value
     */
    @Extension("heightSum")
    public void setHeightWeightSum(JsonValue value) {
        mViewLp.heightWeightSum = value.asInt();
    }

    public void setFocusable(JsonValue value) {
        mView.setFocusable(value.asBoolean());
    }

    public void setClickable(JsonValue value) {
        mView.setClickable(value.asBoolean());
    }

    @Extension("enable")
    public void setEnabled(JsonValue value) {
        mView.setEnabled(value.asBoolean());
    }

    /**
     * 点击事件
     * onClick: add()
     *
     * @param value
     */
    public void setOnClick(final JsonValue value) {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionExec functionExec = new FunctionExec(value, v);
                functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    /**
     * 长按事件
     * onLongClick: add()
     *
     * @param value
     */
    public void setOnLongClick(final JsonValue value) {
        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FunctionExec functionExec = new FunctionExec(value, v);
                functionExec.exec(context, jsChannel, reflect);
                return true;
            }
        });
    }

    /**
     * 焦点事件
     *
     * @param value
     */
    public void setOnFocus(final JsonValue value) {
        mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FunctionExec functionExec = new FunctionExec(value, v, hasFocus);
                functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    /**
     * 设置按键事件
     *
     * @param value
     */
    public void setOnKey(final JsonValue value) {
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                FunctionExec functionExec = new FunctionExec(value, v, keyCode, event);
                return (boolean) functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    /**
     * 触摸事件
     *
     * @param value
     */
    public void setOnTouch(final JsonValue value) {
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FunctionExec functionExec = new FunctionExec(value, v, event);
                return (boolean) functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    @CallSuper
    public void onMeasure(int parentWidth, int parentHeight) {
        Log.i("HView onMeasure", mView.getClass().getSimpleName() + " onMeasure:" + parentWidth + ", " + parentHeight);
        dimens.set(parentWidth, parentHeight).ok();
        for (UiFuncExec uiFuncExec : mUiFuncExecList) {
            uiFuncExec.exec();
        }
        if(isOnReady()) {
            for (UiFuncExec uiFuncExec : mAfterReadyUiFuncExecList) {
                uiFuncExec.exec();
            }
        }
        requestLayout();
    }

    /**
     * 请求布局
     */
    public void requestLayout() {
        mView.requestLayout();
    }

    /**
     * 将内容转换成布局添加到window容器中显示
     */
    public void onLayout() {
    }

    /**
     * 当View被添加到Ui线程中之后执行
     */
    @CallSuper
    protected void onUiReady() {
        for (UiFuncExec uiFuncExec : mAfterReadyUiFuncExecList) {
            uiFuncExec.exec();
        }
        uiReaded = true;
    }

    /**
     * 绘制完毕
     * @return
     */
    public boolean isOnReady() {
        return uiReaded;
    }

    @Override
    /**
     *
     * 当View在AdapterWapper中使用是会被执行该方法
     *
     * @param position 调用getView方法传入的position
     * @param positionData position对应的数据
     * @param privateHTemplate
     */
    public void onAdapterGetView(int position, JsonObject positionData, HTemplate privateHTemplate) {
    }


    @Override
    @CallSuper
    public void onRecycle() {
        if(msgHandler != null) {
            if(msgEnable) {
                msgHandler.removeListener(callback);
                callback = null;
            }
            msgHandler = null;
        }
        super.onRecycle();
        oxpecker = null;
    }

    // 能否发送消息
    private  boolean msgEnable;
    private MsgHandler msgHandler;
    private MsgHandler.Callback callback;
    private JsonValue msgHandlerCallbackFunction;


    /**
     * js MsgHandler监听
     * @param value
     */
    public void setMsgCallback(JsonValue value) {
        this.msgHandlerCallbackFunction = value;
    }

    /**
     *
     *
     * 是否使用消息
     * @param value
     */
    public void setMsgEnable(JsonValue value) {
        setMsgEnable(value.asBoolean());
    }

    public void setMsgEnable(boolean msgEnable) {
        this.msgEnable = msgEnable;
    }

    @CallSuper
    public void setMsgHandler(MsgHandler msgHandler) {
        if(callback != null) {
            msgHandler.removeListener(callback);
        }
        this.msgHandler = msgHandler;
        if(msgEnable) {
            this.msgHandler.addListener(callback = new MsgHandler.Callback() {
                @Override
                public void onHandleMsg(Message msg) {
                    HView.this.onHandleMsg(msg);
                }
            });
        }
    }

    /**
     *
     * 发送消息
     * @param msg
     */
    @Override
    public final void sendMsg(Message msg) {
        if(msgHandler != null) {
            msgHandler.sendMsg(msg);
        }
    }

    /**
     *
     *
     * 接收到消息
     * @param msg
     */
    protected void onHandleMsg(Message msg) {
        if(this.msgHandlerCallbackFunction != null) {
            FunctionExec functionExec = new FunctionExec(msgHandlerCallbackFunction, msg);
            functionExec.exec(context, getJsChannel(), getReflect());
        }
    }
}