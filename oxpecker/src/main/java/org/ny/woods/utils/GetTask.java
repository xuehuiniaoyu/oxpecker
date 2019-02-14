package org.ny.woods.utils;

import android.content.Context;

import org.ny.woods.exception.HException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Get请求工具，通过Uri方式请求IO流到本地生成一个缓存文件
 * 通过解析缓存文件返回Content内容
 * 之后立即销毁文件
 */
public class GetTask {

    public interface OnResourceLoadListener {
        void onLoadFail(String uri, Exception e);
        void onLoadSucc(String uri, String resource);
    }

    private Context context;
    private String uri;
    private OnResourceLoadListener onResourceLoadListener;
    private Reflect reflect = new Reflect();

    public GetTask(Context context, String uri) {
        this.context = context;
        this.uri = uri;
    }

    public GetTask setOnResourceLoadListener(OnResourceLoadListener onResourceLoadListener) {
        this.onResourceLoadListener = onResourceLoadListener;
        return this;
    }

    public void load() {
        if(uri.contains("http://") || uri.contains("https://")) {

            Request request = new Request.Builder().url(uri).get().build();
            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onResourceLoadListener.onLoadFail(uri, e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    onResourceLoadListener.onLoadSucc(uri, response.body().string());
                }
            });
        }
        else {
            InputStream inputStream = null;
            if(uri.contains("/android_asset/")) {
                try {
                    inputStream = context.getAssets().open(uri.split("/android_asset/")[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(uri.contains("/raw/")) {
                int resId = reflect.clear().on(context.getPackageName() + ".R$raw").get(uri.split("/raw/")[1]);
                inputStream = context.getResources().openRawResource(resId);
            }
            else if(uri.contains("/xml/")) {
                int resId = reflect.clear().on(context.getPackageName() + ".R$xml").get(uri.split("/xml/")[1]);
                inputStream = context.getResources().openRawResource(resId);
            }
            else {
                if(uri.contains("file://")) {
                    try {
                        inputStream = new FileInputStream(new File(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(inputStream != null) {
                onResourceLoadListener.onLoadSucc(uri, readStringFromInputStream(inputStream));
            }
            else {
                onResourceLoadListener.onLoadFail(uri, new HException("Failed to load resource"));
            }
        }
    }

    String readStringFromInputStream(InputStream inputStream) {
        StringBuffer stringBuffer = new StringBuffer();
        int len;
        byte[] b = new byte[1024];
        try {
            while ((len = inputStream.read(b)) != -1) {
                stringBuffer.append(new String(b, 0, len));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
