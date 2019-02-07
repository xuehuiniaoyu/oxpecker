package org.ny.woods.layout.widget.i;

import android.content.Context;
import android.view.View;

import org.hjson.JsonObject;
import org.ny.woods.js.channel.JsChannel;

/**
 * 可用于给用户返回
 *
 * @param <T>
 */
public interface ViewPart<T extends View> {
    /**
     * 当View在ListView或GridView中使用会被执行
     * @param position
     * @param positionData
     */
    void onAdapterGetView(int position, JsonObject positionData);

    T getView();
    void onRecycle();

    /**
     * 设置JavaScript通道
     * @param jsChannel
     */
    void setJsChannel(JsChannel jsChannel);
    JsChannel getJsChannel();

    Context getContext();
}
