package org.ny.woods.layout.widget.android_native;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class CanvasView extends View {

    /**
     *
     * 回调监听
     */
    public static abstract class Listener<T> {
        private T obj;
        public Listener(T obj) {
            this.obj = obj;
        }

        public abstract void onDraw(T obj, Canvas canvas);

        private void onDraw(Canvas canvas) {
            onDraw(obj, canvas);
        }
    }

    private Listener listener;
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 绑定绘制回调函数
     * @param listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(listener != null) {
            listener.onDraw(canvas);
        }
    }
}
