package org.ny.woods.js.native_object.console;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.ny.woods.js.native_object.NativeObjInfo;


public class Console extends NativeObjInfo {

    public static final String TAG = Console.class.getSimpleName();

    static final int TOAST = 0;

    private Handler mHandler;

    public Console(Context context) {
        super(context);
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TOAST:
                        Toast.makeText(getContext(),  msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    /**
     *
     * 打印内容到控制台
     * @param msg
     */
    public void print(Object msg) {
        System.out.println(msg+"");
    }

    public void log(Object msg) {
        Log.i(TAG, msg+"");
    }

    /**
     *
     * 弹出层显示内容
     * @param msg
     */
    public void toast(Object msg) {
        mHandler.sendMessage(mHandler.obtainMessage(TOAST, msg+""));
    }
}
