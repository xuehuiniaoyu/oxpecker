package org.ny.woods.layout.adapter;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.dimens.HDimens;
import org.ny.woods.exception.HLayoutException;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.parser.Oxpecker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * JsonArray结构 适配器
 * @see JsonArray
 * 支持传入 JsonArray对象的数组集合，并通过绑定js方法来构建View
 *
 * @see #getView(int, View, ViewGroup)
 */
public class JsonArrayAdapter extends BaseAdapter {
//    /** js通道 */
//    private JsChannel js;
    // 布局hjson格式
    private String layoutString;

    private Oxpecker oxpecker;

    HashMap<View, HView<?>> mHViewCache = new HashMap<>();

    /**
     *
     * 数据源
     * @see #setData(JsonArray)
     */
    private JsonArray array;

    public JsonArrayAdapter(HView<? extends View> hView) {
        onToolsClone(hView);
    }

    protected void onToolsClone(HView<? extends View> hView) {
        int columns = 1;
        if(hView.getView() instanceof GridView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                columns = ((GridView)hView.getView()).getNumColumns();
            }
        }
        HDimens hDimens = hView.getDimens().newHDimens();
        hDimens.set(hView.getView().getWidth()/columns, hView.getView().getHeight());
        hDimens.setLocked(true);
        setOxpecker(new Oxpecker(hView.getOxpecker()).setDimens(hDimens));
    }


    /**
     *
     * 绑定数据
     * @param array
     */
    public JsonArrayAdapter setData(JsonArray array) {
        this.array = array;
        return this;
    }

    /**
     * 设置布局
     * @return
     */
    public JsonArrayAdapter setLayout(String viewLayoutString) {
        this.layoutString = viewLayoutString;
        return this;
    }

    public JsonArrayAdapter setOxpecker(Oxpecker oxpecker) {
        this.oxpecker = oxpecker;
        return this;
    }

    @Override
    public int getCount() {
        return this.array.size();
    }

    @Override
    public JsonObject getItem(int position) {
        return this.array.get(position).asObject();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 绑定数据
     * @param position 元素位置
     * @param convertView 传递过来的View 可能被重用
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HView<? extends View> hView;
        if (convertView == null) {
            try {
                hView = oxpecker.parse(layoutString);
                mHViewCache.put(convertView=hView.getView(), hView);
            } catch (IOException e) { throw new HLayoutException(e.getMessage()); }
        }


//        if(getViewJsFunctionName != null && js != null) {
//
//            /*
//             * 方法名可变
//             * 方法格式不变，参数名可变
//             * adapter 适配器
//             * position 元素位置
//             * convertView 传递过来的View 可能被重用
//             * holder HashMap<Object, Object> 用来存放缓存对象
//             * function getView(adapter, position, convertView, holder) {}
//             **/
//            if(convertView.getTag() == null) {
//                convertView.setTag(new HashMap());
//            }
//            return (View) js.exFunction(getViewJsFunctionName, JsonArrayAdapter.this, position, convertView, convertView.getTag());
//        }

        JsonObject item = getItem(position);
        Iterator<JsonObject.Member> iterator = item.iterator();
        HashMap<String, Object> applyData = new HashMap<>();
        while (iterator.hasNext()) {
            JsonObject.Member member = iterator.next();
            JsonValue value = member.getValue();
            if(value.isString()) {
                applyData.put(member.getName(), value.asString());
            }
            else if(value.isNumber()) {
                applyData.put(member.getName(), value.asDouble());
            }
            else {
                applyData.put(member.getName(), value);
            }
        }

        mHViewCache.get(convertView).onAdapterGetView(applyData);
        return convertView;
    }
}
