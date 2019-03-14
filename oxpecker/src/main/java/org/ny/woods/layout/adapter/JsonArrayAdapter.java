package org.ny.woods.layout.adapter;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.ny.woods.BuildConfig;
import org.ny.woods.dimens.HDimens;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.layout.widget.base.AdapterWapper;
import org.ny.woods.layout.widget.i.ViewPart;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.HTemplate;

import java.util.HashMap;

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

    HashMap<View, ViewPart<?>> mHViewCache = new HashMap<>();

    private HTemplate privateHTemplate;
    private AdapterWapper<? extends AbsListView> adapterWapper;

    /**
     *
     * 数据源
     * @see #setData(JsonArray)
     */
    private JsonArray array;

    public JsonArrayAdapter(AdapterWapper<? extends AbsListView> adapterWapper) {
        onToolsClone(this.adapterWapper = adapterWapper);
        privateHTemplate = new HTemplate();
    }

    protected void onToolsClone(HView<? extends View> hView) {
        int columns = 1;
        if(hView.getView() instanceof GridView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                columns = ((GridView)hView.getView()).getNumColumns();
            }
        }
        HDimens hDimens = hView.getDimens().newHDimens();
        hDimens.set(this.adapterWapper.getView().getWidth()/columns, this.adapterWapper.getView().getHeight());
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
        ViewPart<? extends View> hView;
        if (convertView == null) {
            hView = oxpecker.parse(layoutString);
            mHViewCache.put(convertView=hView.getView(), hView);
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
        mHViewCache.get(convertView).onAdapterGetView(position, getItem(position), privateHTemplate);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            convertView.setLayoutParams(new AbsListView.LayoutParams(-1, -1));
        }
        return convertView;
    }

    public void onRecycle() {
        this.adapterWapper = null;
    }
}
