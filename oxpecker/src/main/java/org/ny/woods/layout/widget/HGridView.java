package org.ny.woods.layout.widget;

import android.content.Context;
import android.widget.GridView;

import org.hjson.JsonValue;
import org.ny.woods.annotations.Extension;
import org.ny.woods.layout.tools.UiFuncExec;
import org.ny.woods.layout.widget.base.AdapterWapper;

public class HGridView extends AdapterWapper<GridView> {
    public HGridView(Context context, JsonValue value) {
        super(context, value);
    }

    public void setColumns(JsonValue value) {
        mView.setNumColumns(value.asInt());
    }

    /**
     *
     * 设置行间距
     * verticalSpacing: 1
     * @param value
     */

    @Extension("vGap")
    public void setVerticalSpacing(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setVerticalSpacing", value) {
            @Override
            protected void onExec(JsonValue value) {
                mView.setVerticalSpacing((int) getDimens().getHeight(value).getSize());
            }
        });
    }

    /**
     *
     * 设置列间距
     * horizontalSpacing: 1
     * @param value
     */
    @Extension("hGap")
    public void setHorizontalSpacing(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setHorizontalSpacing", value) {
            @Override
            protected void onExec(JsonValue value) {
                mView.setHorizontalSpacing((int) getDimens().getHeight(value).getSize());
            }
        });
    }
}
