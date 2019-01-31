package org.ny.woods.layout.widget;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import org.hjson.JsonValue;
import org.ny.woods.annotations.Extension;
import org.ny.woods.layout.widget.base.GroupWapper;
import org.ny.woods.utils.IDUtil;

public class HLinearLayout extends GroupWapper<RelativeLayout> {
    private String orientation = "vertical";
    public HLinearLayout(Context context, JsonValue value) {
        super(context, value);
    }

    @Override
    public void onLayout() {
        if(getChildren() != null) {
            int len = getChildren().size();
            HView<? extends View> positionHNode = null;
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                if(layoutHNode.getView().getId() == View.NO_ID) {
                    layoutHNode.getView().setId(IDUtil.id(mView.hashCode() + ".id+" + i));
                }
                if(positionHNode != null) {
                    setOrientation(positionHNode, layoutHNode);
                }
                mView.addView((positionHNode = layoutHNode).getView());
            }
        }
    }

    /**
     *
     *
     * 设置方向
     * orientation: vertical
     * @param value
     */
    @Extension("orien")
    public void setOrientation(JsonValue value) {
        this.orientation = value.asString();
    }

    private void setOrientation(HView<? extends View> prev, HView<? extends View> next) {
        int preId = prev.getView().getId();
        switch (this.orientation) {
            case "vertical":
            case "v":
            {
                next.mViewLp.addRule(RelativeLayout.BELOW, preId);
                break;
            }
            case "horizontal":
            case "h":
            {
                next.mViewLp.addRule(RelativeLayout.RIGHT_OF, preId);
                break;
            }
        }
    }

    @Override
    public void setWidth(JsonValue value) {
        super.setWidth(value);
    }

    @Override
    public void setHeight(JsonValue value) {
        super.setHeight(value);
    }
}
