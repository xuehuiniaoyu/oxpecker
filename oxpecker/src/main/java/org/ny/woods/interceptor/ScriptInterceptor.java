package org.ny.woods.interceptor;

import android.content.Context;
import android.util.Log;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.dimens.HDimens;
import org.ny.woods.exception.HException;
import org.ny.woods.interceptor.i.HInterceptor;
import org.ny.woods.js.channel.JsChannel;
import org.ny.woods.utils.GetTask;

import java.util.HashMap;
import java.util.Iterator;

public class ScriptInterceptor implements HInterceptor<ScriptInterceptor> {
    public static final String TAG = ScriptInterceptor.class.getSimpleName();

    public interface OnScriptLoadListener {
        /**
         * 准备就绪
         */
        int STATE_READY = 0;

        /**
         *
         * 下载中
         */
        int STATE_DLOADING = 1;

        /**
         *
         * 下载成功
         */
        int STATE_SUCCESS = 2;

        /**
         *
         * 下载失败
         */
        int STATE_FAIL = 3;

        /**
         *
         * 重新下载
         */
        int RE_LOAD = 4;

        /**
         *
         * 加载完成
         */
        int STATE_ON_LOAD = 5;
        void onCallback(int state, String uri, Exception error);
    }

    /**
     *
     * 脚本队列，文件中配置的脚本代码会有序下载并执行
     */
    String[] scriptQueue;
    HashMap<Integer, String> scriptUri;
    private JsChannel jsChannel;

    /**
     *
     * 监听
     */
    private OnScriptLoadListener onScriptLoadListener = new OnScriptLoadListener() {
        @Override
        public void onCallback(int state, String uri, Exception error) {
            Log.i(TAG, uri + "==========" + state+", error:"+error);
        }
    };

    public void setOnScriptLoadListener(OnScriptLoadListener onScriptLoadListener) {
        this.onScriptLoadListener = onScriptLoadListener;
    }

    @Override
    public ScriptInterceptor onInterceptor(Context context, HDimens dimens, JsonObject jsonObject) {
        JsonValue head = jsonObject.get("head");
        if (head != null) {
            Iterator<JsonObject.Member> iterator = head.asObject().iterator();
            if (iterator.hasNext()) {
                onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_READY, null, null);
                while (iterator.hasNext()) {
                    JsonObject.Member member = iterator.next();
                    switch (member.getName()) {
                        case "script": {
                            if(scriptQueue == null) {
                                scriptQueue = new String[1];
                            }
                            else {
                                String[] tmp = new String[scriptQueue.length+1];
                                System.arraycopy(scriptQueue, 0, tmp, 0, scriptQueue.length);
                                scriptQueue = tmp;
                            }
                            JsonValue script = member.getValue();
                            if (script.isObject()) {
                                String uri = script.asObject().getString("src", null);
                                if(scriptUri == null) {
                                    scriptUri = new HashMap<>();
                                }
                                scriptUri.put(scriptQueue.length-1, uri);
                                onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_DLOADING, uri, null);
                                final GetTask task = new GetTask(context, uri);
                                GetTask.OnResourceLoadListener listener = new GetTask.OnResourceLoadListener() {
                                    int id = scriptQueue.length-1;
                                    int failCount = 3;
                                    @Override
                                    public void onLoadFail(String uri, Exception e) {
                                        onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_FAIL, uri, e);
                                        if( (--failCount) == 0 ) {
                                            scriptQueue[id] = "error!";
                                            tryToLoadScript();
                                        }
                                        else {
                                            onScriptLoadListener.onCallback(OnScriptLoadListener.RE_LOAD, uri, e);
                                            task.load();
                                        }
                                    }

                                    @Override
                                    public void onLoadSucc(String uri, String resource) {
                                        scriptQueue[id] = resource+"\n";
                                        onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_SUCCESS, uri, null);
                                        tryToLoadScript();
                                    }
                                };
                                task.setOnResourceLoadListener(listener);
                                task.load();
                            } else {
                                int id = scriptQueue.length-1;
                                scriptQueue[id] = script.asString()+"\n";
                                onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_SUCCESS, null, null);
                            }
                            break;
                        }
                    }
                }
                return this;
            } else {
                return null;
            }
        }

//        String script = jsonObject.getString("script", null);
        return null;
    }

    private void tryToLoadScript() {
        if (this.jsChannel != null) {
            if (scriptQueue != null) {
                int errorIndex = -1;
                boolean over = true;
                StringBuffer buffer = new StringBuffer();
                for(int i = 0; i < scriptQueue.length; i++) {
                    String str = scriptQueue[i];
                    if(str == null) {
                        over = false;
                        break;
                    }
                    if("error!".equals(str)) {
                        errorIndex = i;
                    }
                    buffer.append(str);
                }
                if(over) {
                    String js = buffer.toString();
                    jsChannel.loadJs(js);
                    if(errorIndex == -1) {
                        onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_ON_LOAD, null, null);
                    }
                    else {
                        onScriptLoadListener.onCallback(OnScriptLoadListener.STATE_ON_LOAD, null, new HException("script["+scriptUri.get(errorIndex)+"][error!]"));
                    }
                }
            } else {
                Log.i(TAG, "not over!");
            }
        }
    }

    public void setJsChannel(JsChannel jsChannel) {
        this.jsChannel = jsChannel;
        tryToLoadScript();
    }

}
