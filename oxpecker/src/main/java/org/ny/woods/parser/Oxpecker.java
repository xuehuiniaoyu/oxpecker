package org.ny.woods.parser;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;

import org.ny.woods.dimens.HDimens;
import org.ny.woods.dimens.PercentDimens;
import org.ny.woods.exception.HLayoutException;
import org.ny.woods.interceptor.BodyInterceptor;
import org.ny.woods.interceptor.ScriptInterceptor;
import org.ny.woods.js.channel.JsChannel;
import org.ny.woods.js.channel.SilentlyJsChannel;
import org.ny.woods.js.native_object.code.JavaPackage;
import org.ny.woods.js.native_object.color.ColorManager;
import org.ny.woods.js.native_object.console.Console;
import org.ny.woods.js.native_object.net.Net;
import org.ny.woods.js.native_object.reflect.RobustReflect;
import org.ny.woods.js.native_object.util.Utils;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.template.HTemplate;
import org.ny.woods.template.SimpleHTemplate;
import org.ny.woods.utils.IDUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Oxpecker {

    public interface OnPackListener {
        void onFailed(Exception e, Oxpecker oxpecker);
        void onSuccess(HView<? extends View> hView, Oxpecker oxpecker);
    }

    public static final class OxpeckerType<T> {
        T type;
        HView<? extends View> hView;
        String layout;
        Exception err;
        OnPackListener onPackListener;

        public OxpeckerType(T type) {
            this.type = type;
        }
    }

    public void inflaterAsync(OxpeckerType<?> oxpeckerType, OnPackListener onPackListener) {
        oxpeckerType.onPackListener = onPackListener;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            final AsyncTask<OxpeckerType<?>, Void, OxpeckerType<?>> asyncTask
                    = new AsyncTask<OxpeckerType<?>, Void, OxpeckerType<?>>() {
                @Override
                protected OxpeckerType<?> doInBackground(OxpeckerType<?>... inflaterTypes) {
                    OxpeckerType<?> oxpeckerType = inflaterTypes[0];
                    if(oxpeckerType.type instanceof File) {
                        try {
                            oxpeckerType.layout = hTemplate.apply(new FileInputStream((File) oxpeckerType.type));
                        } catch (IOException e) {
                            oxpeckerType.err = e;
                        }
                    }
                    else if(oxpeckerType.type instanceof String) {
                        try {
                            oxpeckerType.layout = hTemplate.apply((String) oxpeckerType.type);
                        } catch (IOException e) {
                            oxpeckerType.err = e;
                        }
                    }
                    else if(oxpeckerType.type instanceof InputStream) {
                        try {
                            oxpeckerType.layout = hTemplate.apply((InputStream) oxpeckerType.type);
                        } catch (IOException e) {
                            oxpeckerType.err = e;
                        }
                    }
                    oxpeckerType.hView = inflater(oxpeckerType.layout);
                    return oxpeckerType;
                }

                @Override
                protected void onPostExecute(OxpeckerType<?> inflaterType) {
                    if(inflaterType.err != null) {
                        inflaterType.onPackListener.onFailed(inflaterType.err, Oxpecker.this);
                    }
                    else {
                        inflaterType.onPackListener.onSuccess(inflaterType.hView, Oxpecker.this);
                    }
                }
            };
            asyncTask.execute(oxpeckerType);
        }
    }


    public static View findViewById(Activity activity, String id) {
        return activity.findViewById(IDUtil.id(id));
    }

    public static View findViewById(View parent, String id) {
        return parent.findViewById(IDUtil.id(id));
    }

    /**
     *
     * 抽象的父容器宽度
     * 不管屏幕宽度是多少，你已设定值为准
     */
    private int scaleWidth;
    /**
     *
     * 抽象的父容器高度
     * 不管屏幕高度是多少，你已设定值为准
     */
    private int scaleHeight;

    private Context context;
    private HTemplate hTemplate;

    private HDimens dimens;


    /**
     * 布局解析器
     */
    private BodyInterceptor mBodyInterceptor = new BodyInterceptor();
    private ScriptInterceptor mScriptInterceptor;


    private HView<? extends View> hView;
    private SilentlyJsChannel mSilentlyJsChannel;

    /**
     *
     *
     * 异步解析回调接口
     */
    private OnPackListener onPackListener;

    public Oxpecker(Oxpecker oxpecker) {
        this.context = oxpecker.context;
        setTemplate(oxpecker.hTemplate);
        this.hTemplate.as("context", context);
        this.scaleWidth = oxpecker.scaleWidth;
        this.scaleHeight = oxpecker.scaleHeight;
        this.mJavaScriptInterface.putAll(oxpecker.mJavaScriptInterface);
        this.mBodyInterceptor = oxpecker.mBodyInterceptor;
    }

    public Oxpecker setTemplate(HTemplate hTemplate) {
        this.hTemplate = new HTemplate(hTemplate);
        this.hTemplate.as("package", context.getPackageName());
        this.hTemplate.as("assets", "file:///android_asset");
        this.hTemplate.as("raw", "android.resource://"+context.getPackageName()+"/raw");
        this.hTemplate.as("drawable", "android.resource://"+context.getPackageName()+"/drawable");
        this.hTemplate.as("sdcard", "file://"+Environment.getExternalStorageDirectory().getPath());
        this.hTemplate.as("file", Uri.fromFile(context.getFilesDir()).getPath());
        this.hTemplate.as("cache", Uri.fromFile(context.getCacheDir()).getPath());
        this.hTemplate.as("/", Uri.fromFile(context.getFilesDir().getParentFile()).getPath());
        return this;
    }

    /**
     *
     * 替换
     * @param name
     * @param template
     * @return
     */
    public Oxpecker apply(String name, String template) {
        try {
            hTemplate.as(name, hTemplate.apply(template));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     *
     *
     * java对象映射到javascript
     */
    private HashMap<String, Object> mJavaScriptInterface = new HashMap<>();

    public Oxpecker(Context context) {
        this(context, new SimpleHTemplate(), 100, 100);
    }

    public Oxpecker(Context context, int scaleWidth, int scaleHeight) {
        this(context, new SimpleHTemplate(), scaleWidth, scaleHeight);
    }

    public Oxpecker(Context context, HTemplate hTemplate) {
        this(context, hTemplate, 100, 100);
    }

    public Oxpecker(Context context, HTemplate hTemplate, int scaleWidth, int scaleHeight) {
        this.context = context;
        setTemplate(hTemplate);
        this.hTemplate.as("context", context);
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
    }

    public Oxpecker addJavaScriptInterface(String name, Object object) {
        mJavaScriptInterface.put(name, object);
        return this;
    }

    public Oxpecker setDimens(HDimens dimens) {
        this.dimens = dimens;
        return this;
    }

//    private HInterceptor[] hInterceptors = new HInterceptor[] {
//
//            new BodyInterceptor(),
//            new ScriptInterceptor(),
//
//    };

    public HView<? extends View> inflater(String layout) {
        //layout = layout.replace("&quot;", "\"");
        HLayoutManager groupManager = new HLayoutManager(context);
        Object[] results = groupManager.layout(layout, dimens != null ? dimens : new PercentDimens().scaleTo(scaleWidth, scaleHeight), mBodyInterceptor,  new ScriptInterceptor());
        HView<? extends View> mLayout = (HView<? extends View>) results[0];
        mLayout.setOxpecker(new Oxpecker(this));
        if(results[1] != null) {
            mScriptInterceptor = (ScriptInterceptor) results[1];
            // 添加框架定义的接口对象
            mSilentlyJsChannel = new SilentlyJsChannel();
            mSilentlyJsChannel.addJavaScriptInterface("__context", context);
            mSilentlyJsChannel.addJavaScriptInterface("__reflect", new RobustReflect());
            mSilentlyJsChannel.addJavaScriptInterface("__console", new Console(context));
            mSilentlyJsChannel.addJavaScriptInterface("__net", new Net(context));
            mSilentlyJsChannel.addJavaScriptInterface("__package", new JavaPackage());
            mSilentlyJsChannel.addJavaScriptInterface("__utils", new Utils(context));
            mSilentlyJsChannel.addJavaScriptInterface("__color", new ColorManager(context));
            // 添加自定义接口对象
            for (String name : mJavaScriptInterface.keySet()) {
                mSilentlyJsChannel.addJavaScriptInterface(name, mJavaScriptInterface.get(name));
            }
//            mLayout.setJsChannel(mSilentlyJsChannel);
        }
        return hView=mLayout;
    }

    /**
     * 获取View
     * @return
     */
    public HView<? extends View> getView() {
        return hView;
    }

    /**
     * 获取js通道
     * @return
     */
    public JsChannel getJsChannel() {
        return mSilentlyJsChannel;
    }

    /**
     * 初始化
     */
    public void startPecking() {
        if(hView != null && mScriptInterceptor != null) {
            mScriptInterceptor.setJsChannel(mSilentlyJsChannel);
            hView.setJsChannel(mSilentlyJsChannel);
        }
    }

    /**
     * 销毁
     */
    public void finishPecking() {
        if(hView != null) {
            hView.destroy();
            hView = null;
        }
        mSilentlyJsChannel = null;
        hTemplate = null;
    }

    /**
     * 解析布局
     * @param inputStream
     * @return
     * @throws IOException
     */
    public HView<? extends View> parse(InputStream inputStream) throws IOException {
        String template = hTemplate.apply(inputStream);
        return inflater(template);
    }

    /**
     * 解析布局 从文本内容
     * @param source
     * @return
     * @throws IOException
     */
    public HView<? extends View> parse(String source) throws IOException {
        String template = hTemplate.apply(source);
        return inflater(template);
    }

    /**
     * 解析布局 从文件
     * @param file
     * @return
     * @throws IOException
     */
    public HView<? extends View> parse(File file) throws IOException{
        try {
            String template = hTemplate.apply(new FileInputStream(file));
            return inflater(template);
        } catch (IOException e) {
            throw e;
        }
    }

    public HTemplate getTemplate() {
        return hTemplate;
    }

    /**
     *
     * 设置回调
     * @param onPackListener
     */
    public void setOnPackListener(OnPackListener onPackListener) {
        this.onPackListener = onPackListener;
    }

    void checkLayoutInflaterListenerSetted() {
        if(onPackListener == null) {
            throw new HLayoutException("not exec setOnLayoutInflaterListener method");
        }
    }
}
