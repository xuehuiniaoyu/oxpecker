package org.ny.woods.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class HAsyncTask<A, R> {

    public static final int RUNNING = 0;
    public static final int FINISH = 1;
    public static final int CANCEL = 2;

    private int state = -1;

    private Handler mHandler;
    public HAsyncTask(Looper looper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            mHandler = new Handler(looper, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if(state != CANCEL) {
                        state = FINISH;
                        if (notExecArguments != null) {
                            A[] temp = notExecArguments;
                            notExecArguments = null;
                            exec(temp);
                        } else {
                            R result = (R) msg.obj;
                            onPostExecute(result);
                        }
                    }
                    return true;
                }
            });
        }
    }

    Runnable mBackRunnable;

    private A[] arguments;
    /**
     *
     * 没被执行的参数
     */
    private A[] notExecArguments;

    protected abstract R doInBackground(A[] arguments);

    protected abstract void onPostExecute(R result);

    public void exec(A ... arguments) {
        if(state != CANCEL) {
            this.arguments = arguments;
            if (state == -1 || state == FINISH) {
                if (mBackRunnable == null) {
                    mBackRunnable = new Runnable() {
                        @Override
                        public void run() {
                            state = RUNNING;
                            R result = doInBackground(HAsyncTask.this.arguments);
                            mHandler.sendMessage(mHandler.obtainMessage(0, result));
                        }
                    };
                }
                new Thread(mBackRunnable).start();
            } else {
                notExecArguments = arguments;
            }
        }
    }

    public boolean isRunning() {
        return state == RUNNING;
    }

    public void cancel() {
        state = CANCEL;
        mBackRunnable = null;
    }
}
