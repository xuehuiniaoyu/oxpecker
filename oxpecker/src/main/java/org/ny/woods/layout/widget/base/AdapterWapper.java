package org.ny.woods.layout.widget.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import org.hjson.JsonArray;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.ny.woods.js.channel.JsChannel;
import org.ny.woods.layout.adapter.JsonArrayAdapter;
import org.ny.woods.layout.tools.FunctionExec;
import org.ny.woods.layout.tools.UiFuncExec;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.utils.GetTask;
import org.ny.woods.utils.HUri;

import java.io.IOException;

/**
 * 可重用View的包装类
 *
 * @param <T>
 */
public class AdapterWapper<T extends AbsListView> extends HView<T> {
    public static final String TAG = AdapterWapper.class.getSimpleName();

    private String viewLayoutString;
    private boolean viewLayoutStringIsLayout;

    private JsonArrayAdapter arrayAdapter;

    public AdapterWapper(Context context, JsonValue value) {
        super(context, value);
    }

    /**
     * 告知数据来源
     * 可以是hjson格式也可以是uri方式
     *
     * @param value
     * @see JsonArrayAdapter#setData(JsonArray)
     */
    public void setData(JsonValue value) {
        mAfterReadyUiFuncExecList.add(new UiFuncExec<JsonValue>("setData", value) {
            @Override
            protected void onExec(JsonValue value) {
                if (value.isArray()) {
                    Log.i(TAG, "data from layout");
                    loadedDataThenBind(value.asArray());
                } else {
                    Log.i(TAG, "data from uri");
                    String valueString = value.asString();
                    if (HUri.isUri(valueString)) {
                        // 通过uri方式下载数据
                        new GetTask(getContext(), valueString).setOnResourceLoadListener(new GetTask.OnResourceLoadListener() {
                            @Override
                            public void onLoadFail(String uri, Exception e) {
                                Log.e(TAG, uri + " onLoadFail:" + e.getLocalizedMessage());
                            }

                            @Override
                            public void onLoadSucc(String uri, String resource) {
                                loadedDataThenBind(resource);
                            }
                        }).load();
                    }
                }
            }
        });
    }

    /**
     * 从JsonArray对象中解析数据
     *
     * @param array
     */
    void loadedDataThenBind(final JsonArray array) {
        if(!isDied()) {
            if (arrayAdapter == null) {
                if (!viewLayoutStringIsLayout) {

                    new GetTask(getContext(), viewLayoutString).setOnResourceLoadListener(new GetTask.OnResourceLoadListener() {
                        @Override
                        public void onLoadFail(String uri, Exception e) {
                            Log.e(TAG, uri + " onLoadFail:" + e.getLocalizedMessage());
                        }

                        @Override
                        public void onLoadSucc(String uri, String resource) {
                            if (!isDied()) {
                                viewLayoutString = resource;
                                mView.setAdapter(arrayAdapter = new JsonArrayAdapter(AdapterWapper.this).setData(array)
                                        .setLayout(viewLayoutString));
                            }
                        }
                    }).load();
                } else {
                    mView.setAdapter(arrayAdapter = new JsonArrayAdapter(AdapterWapper.this).setData(array)
                            .setLayout(viewLayoutString));
                }
            } else {
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 从文本中解析数据
     *
     * @param hjson
     */
    void loadedDataThenBind(String hjson) {
        try {
            hjson = getOxpecker().getTemplate().apply(hjson);
        } catch (IOException e) {}
        JsonArray data = JsonValue.readHjson(hjson).asArray();
        loadedDataThenBind(data);
    }

    /**
     * 告知重用的View布局来源
     * 可以是hjson格式也可以是uri方式
     *
     * @param value
     */
    public void setView(JsonValue value) {
        if (value.isObject()) {
            viewLayoutString = value.toString(Stringify.HJSON);
            viewLayoutStringIsLayout = true;
        } else {
            viewLayoutString = value.asString();
            viewLayoutStringIsLayout = false;
        }
    }

    @Override
    public void setJsChannel(JsChannel jsChannel) {
        super.setJsChannel(jsChannel);
    }

    /**
     * 点击事件
     * @param value
     */
    public void setOnItemClick(final JsonValue value) {
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FunctionExec functionExec = new FunctionExec(value, parent, view, position, id);
                functionExec.exec(getContext(), getJsChannel(), getReflect());
            }
        });
    }

    /**
     * 长按事件
     * @param value
     */
    public void setOnItemLongClick(final JsonValue value) {
        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        FunctionExec functionExec = new FunctionExec(value, parent, view, position, id);
                        functionExec.exec(getContext(), getJsChannel(), getReflect());
                        return true;
                    }
                });
                return true;
            }
        });
    }

    /**
     * 焦点事件
     * @param value
     */
    public void setOnItemSelected(final JsonValue value) {
        mView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view != null) {
                    FunctionExec functionExec = new FunctionExec(value, parent, view, position, id);
                    functionExec.exec(getContext(), getJsChannel(), getReflect());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 设置样式选择器
     *
     * @param value
     */
    public void setListSelector(JsonValue value) {
        String valueString = value.asString();
        if ("@null".equals(valueString)) {
            mView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        } else {
            mView.setSelector(getResourceId(valueString));
        }
    }

    /**
     * 设置样式选择器的显示方式
     *
     * @param value
     */
    public void setDrawSelectorOnTop(JsonValue value) {
        mView.setDrawSelectorOnTop(value.asBoolean());
    }
}
