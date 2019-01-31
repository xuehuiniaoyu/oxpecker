package org.ny.woods.js.channel;

import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.ny.woods.utils.ErrorUtil;

/**
 * Created by xjy on 2016/11/14.
 * js 通道，在Rhino引擎上做了进一步封装，能够实现java和js通信。
 *
 */
public class JsChannel {

    Context rhino;
    Scriptable scope;

    {
        rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        scope = rhino.initStandardObjects();
    }

    /**
     * 添加接口对象
     * @param name
     * @param object
     */
    public final void addJavaScriptInterface(String name, Object object){
        ScriptableObject.putProperty(scope, name, Context.javaToJS(object, scope));
    }

    /**
     * 移除接口对象
     * @param name
     */
    public final void removeJavaScriptInterface(String name){
        ScriptableObject.deleteProperty(scope, name);
    }

    /**
     * 加载js
     * @param js
     * @return
     */
    public Object loadJs(String js){
        try {
            return rhino.evaluateString(scope, js, "loadJs", 1, null);
        } catch (Exception e) {
            Log.e("JsChannel", "load js error ---- "+ ErrorUtil.ex(e));
            return null;
        } finally {
        }
    }

    public Object exJs(String js){
        Context ct = Context.enter();
        Scriptable scope = ct.initStandardObjects();
        Object obj = ct.evaluateString(scope, js, null, 1, null);
        Context.exit();
        return obj;
    }

    /**
     * 执行方法
     * @param function
     * @param args
     * @return
     */
    public Object exFunction(String function, Object... args){
        if(function != null) {
            try {
                System.out.println("function=" + function);
                Function f = (Function) scope.get(function, scope);
                Object result = f.call(rhino, scope, scope, args);
                if (result instanceof NativeJavaObject) {
                    return ((NativeJavaObject) result).getDefaultValue(String.class).toString();
                } else if (result instanceof NativeObject) {
                    return ((NativeObject) result).getDefaultValue(String.class).toString();
                }
                return result;
            } catch (Exception e) {
                Log.e("JsChannel", "没找到方法 " + function + " ---- " + ErrorUtil.ex(e));
                return null;
            }
        }
        return null;
    }

    /*public String getText(){
        return "123";
    }


    public static void main(String[] args) {
        JsChannel channel = new JsChannel();
        channel.addJavaScriptInterface("context", channel);
        Object obj = channel.loadJs("var dex = function(){}; dex.ddd = context; function a(){return dex.ddd.getText().getClass();}");
        obj = channel.exFunction("a", new String[]{});
        System.out.println("obj = "+obj);
        Object obj1 = channel.loadJs("function b(arg, b){return context.getText()+' hello world'+arg+b;}");
        obj1 = channel.exFunction("b", new Object[]{'a', 1});
        System.out.println("obj1 = "+obj1);
    }*/
}
