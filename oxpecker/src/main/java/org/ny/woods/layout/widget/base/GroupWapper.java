package org.ny.woods.layout.widget.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.js.channel.JsChannel;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.HTemplate;

public class GroupWapper<T extends ViewGroup> extends HView<T> {
    public GroupWapper(Context context, JsonValue value) {
        super(context, value);
    }

    public void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.onMeasure(mViewLp.width > 0 ? mViewLp.width : getDimens().getParentWidth(), mViewLp.height > 0 ? mViewLp.height : getDimens().getParentHeight());
            }
        }
    }

    @Override
    public void onLayout() {
        super.onLayout();
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                mView.addView(layoutHNode.getView());
            }
        }
    }

    @Override
    public void setJsChannel(JsChannel jsChannel) {
        super.setJsChannel(jsChannel);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.setJsChannel(jsChannel);
            }
        }
    }

    @Override
    public void setOxpecker(Oxpecker oxpecker) {
        super.setOxpecker(oxpecker);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.setOxpecker(oxpecker);
            }
        }
    }

    @Override
    public void onAdapterGetView(int position, JsonObject positionData, HTemplate privateHTemplate) {
        super.onAdapterGetView(position, positionData, privateHTemplate);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.onAdapterGetView(position, positionData, privateHTemplate);
            }
        }
    }
}
