package org.ny.woods.layout.widget;

import android.content.Context;
import android.widget.ListView;

import org.hjson.JsonValue;
import org.ny.woods.layout.widget.base.AdapterWapper;

public class HListView extends AdapterWapper<ListView> {
    public HListView(Context context, JsonValue value) {
        super(context, value);
    }
}
