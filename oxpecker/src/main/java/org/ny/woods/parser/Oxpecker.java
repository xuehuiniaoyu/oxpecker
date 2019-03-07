package org.ny.woods.parser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.ny.woods.dimens.HDimens;
import org.ny.woods.dimens.PercentDimens;
import org.ny.woods.exception.HException;
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
import org.ny.woods.layout.widget.i.ViewPart;
import org.ny.woods.os.Message;
import org.ny.woods.parser.proxy.DynamicProxy;
import org.ny.woods.template.HTemplate;
import org.ny.woods.template.SimpleHTemplate;
import org.ny.woods.utils.IDUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class Oxpecker {
    public static final class AsyncTytpe {

        /**
         * 回调接口
         *
         *
         *
         */
        public interface Callback {
            void onFailed(Exception e, Oxpecker oxpecker);
            void onSuccess(ViewPart<? extends View> hView, Oxpecker oxpecker);
        }

        public static final int FILE = 0;
        public static final int STRING = 1;
        public static final int STREAM = 2;

        int type;
        Object obj;

        ViewPart<? extends View> hView;
        String layout;
        Exception err;
        Callback callback;

        public AsyncTytpe(Object obj, int type) {
            this.obj = obj;
            this.type = type;
        }
    }

    /**
     * 异步加载View
     * @param asyncTytpe
     * @param callback
     */
    public void inflaterAsync(AsyncTytpe asyncTytpe, AsyncTytpe.Callback callback) {
        asyncTytpe.callback = callback;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            final AsyncTask<AsyncTytpe, Void, AsyncTytpe> asyncTask
                    = new AsyncTask<AsyncTytpe, Void, AsyncTytpe>() {
                @Override
                protected AsyncTytpe doInBackground(AsyncTytpe... inflaterTypes) {
                    AsyncTytpe asyncTytpe = inflaterTypes[0];
                    switch (asyncTytpe.type) {
                        case AsyncTytpe.FILE: {
                            try {
                                asyncTytpe.layout = hTemplate.apply(new FileInputStream((File) asyncTytpe.obj));
                            } catch (IOException e) {
                                asyncTytpe.err = e;
                            }
                            break;
                        }
                        case AsyncTytpe.STRING: {
                            try {
                                asyncTytpe.layout = hTemplate.apply((String) asyncTytpe.obj);
                            } catch (IOException e) {
                                asyncTytpe.err = e;
                            }
                            break;
                        }
                        case AsyncTytpe.STREAM: {
                            try {
                                asyncTytpe.layout = hTemplate.apply((InputStream) asyncTytpe.obj);
                            } catch (IOException e) {
                                asyncTytpe.err = e;
                            }
                            break;
                        }
                    }
                    asyncTytpe.hView = inflater(asyncTytpe.layout);
                    return asyncTytpe;
                }

                @Override
                protected void onPostExecute(AsyncTytpe inflaterType) {
                    if(inflaterType.err != null) {
                        inflaterType.callback.onFailed(inflaterType.err, Oxpecker.this);
                    }
                    else {
                        inflaterType.callback.onSuccess(inflaterType.hView, Oxpecker.this);
                    }
                }
            };
            asyncTask.execute(asyncTytpe);
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


    private ViewPart<? extends View> hView;
    private SilentlyJsChannel mSilentlyJsChannel;

    public Oxpecker setTemplate(HTemplate hTemplate) {
        this.hTemplate = new HTemplate(hTemplate);
        this.hTemplate.asFinal(this.context);
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

    public Oxpecker(Oxpecker oxpecker) {
        this.context = oxpecker.context;
        setTemplate(oxpecker.hTemplate);
        this.scaleWidth = oxpecker.scaleWidth;
        this.scaleHeight = oxpecker.scaleHeight;
        this.mJavaScriptInterface.putAll(oxpecker.mJavaScriptInterface);
        this.mBodyInterceptor = oxpecker.mBodyInterceptor;
    }

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

    public ViewPart<? extends View> inflater(String layout) {
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
        InvocationHandler handler = new DynamicProxy(mLayout);
        hView = (ViewPart<? extends View>) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{ViewPart.class}, handler);
        return hView;
    }

    /**
     * 获取View
     * @return
     */
    public ViewPart<? extends View> getView() {
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
            hView.onRecycle();
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
    public ViewPart<? extends View> parse(InputStream inputStream) {
        String template;
        try {
            template = hTemplate.apply(inputStream);
        } catch (IOException e) {
            throw new HException(e.getLocalizedMessage());
        }
        return inflater(template);
    }

    /**
     * 解析布局 从文本内容
     * @param source
     * @return
     * @throws IOException
     */
    public ViewPart<? extends View> parse(String source) {
        String template;
        try {
            template = hTemplate.apply(source);
        } catch (IOException e) {
            throw new HException(e.getLocalizedMessage());
        }
        return inflater(template);
    }

    /**
     * 解析布局 从文件
     * @param file
     * @return
     * @throws IOException
     */
    public ViewPart<? extends View> parse(File file){
        String template;
        try {
            template = hTemplate.apply(new FileInputStream(file));
        } catch (IOException e) {
            throw new HException(e.getLocalizedMessage());
        }
        return inflater(template);
    }

    public HTemplate getTemplate() {
        return hTemplate;
    }

    /**
     * 发送全局消息
     * @param msg
     */
    public void sendMsg(Message msg) {
        if(hView != null) {
            hView.sendMsg(msg);
        }
    }
}
