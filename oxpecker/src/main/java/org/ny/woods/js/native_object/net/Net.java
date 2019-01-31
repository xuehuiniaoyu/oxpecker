package org.ny.woods.js.native_object.net;

import android.content.Context;

import org.mozilla.javascript.ScriptableObject;
import org.ny.woods.js.native_object.NativeObjInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Net extends NativeObjInfo {
    public Net(Context context) {
        super(context);
    }

    /**
     * 异步请求
     * @param data
     *
     * <pre>
     * js代码：
     * H.ajax(
     *      // 请求
     *  {
     *     method:'get',
     *     url:'http://www.baidu.com',
     *     body:null 或 function(){
     *
     *     }
     *     或 字符串 或 不填，
     *     contentType:"application/xml或 application/json"
     *
     *
     * },
     *
     *  {
     *      // 响应
     *      success:function（data）{
     *
     *      }，
     *      error:function(data){
     *
     *      }
     *  }
     *
     * )
     *<pre/>
     */
    public void okHttp(final ScriptableObject data){
        Object method = callField(data, "method");
        Object contentType = callField(data, "contentType");
        Object b = ScriptableObject.getProperty(data, "body");
        Object body;
        if(b instanceof String){
            body = callField(data, "body");
        }
        else {
            body = callMethod(data, "body", new Object[]{});
        }
        Object url = callField(data, "url");

        Request.Builder requestBuilder = new Request.Builder().url(url.toString());
        if(contentType == null) {
            contentType = "application/json";
        }
        Request request = null;
        switch (method.toString().toLowerCase()) {
            case "post": {
                request = requestBuilder.post(RequestBody.create(MediaType.parse(contentType.toString()), body.toString())).build();
                break;
            }
            case "get": {
                request = requestBuilder.get().build();
                break;
            }
            case "put": {
                request = requestBuilder.put(RequestBody.create(MediaType.parse(contentType.toString()), body.toString())).build();
                break;
            }
            case "delete": {
                request = requestBuilder.delete(RequestBody.create(MediaType.parse(contentType.toString()), body.toString())).build();
                break;
            }
        }
        if(request != null) {
            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callMethod(data, "error", new Object[]{e});
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callMethod(data, "success", new Object[]{response});
                }
            });
        }
    }

    static synchronized Object callMethod(ScriptableObject obj, String method, Object[] args){
        if(ScriptableObject.hasProperty(obj, method)) {
            return ScriptableObject.callMethod(obj, method, args);
        }
        return null;
    }

    static synchronized Object callField(ScriptableObject obj, String method){
        if(ScriptableObject.hasProperty(obj, method)) {
            return ScriptableObject.getProperty(obj, method);
        }
        return null;
    }
}
