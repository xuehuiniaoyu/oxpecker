package org.ny.woods.layout.widget;

import android.content.Context;
import android.graphics.Canvas;

import org.hjson.JsonValue;
import org.ny.woods.layout.tools.FunctionExec;
import org.ny.woods.layout.widget.android_native.CanvasView;

public class HCanvasView extends HView<CanvasView> {
    public HCanvasView(Context context, JsonValue value) {
        super(context, value);
    }

    /**
     *
     * @param value
     */
    public void setOnDraw(JsonValue value) {
        mView.setListener(new CanvasView.Listener<JsonValue>(value) {
            @Override
            public void onDraw(JsonValue obj, Canvas canvas) {
                FunctionExec functionExec = new FunctionExec(obj, canvas);
                functionExec.exec(getContext(), getJsChannel(), getReflect());
            }
        });
    }

}
