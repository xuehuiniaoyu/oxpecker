package xuehuiniaoyu.github.oxpecker.view;

import android.content.Context;
import android.util.Log;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.template.HTemplate;

import xuehuiniaoyu.github.oxpecker.view.android_native.ProBar;

public class MyProgressBar extends DataBoundView<ProBar> {

    public static final String TAG = MyProgressBar.class.getSimpleName();

    private JsonValue mProgress, mMax;
    private HTemplate hTemplate;

    public MyProgressBar(Context context, JsonValue value) {
        super(context, value);
        hTemplate = new HTemplate();
    }

    public void setProgress(JsonValue value) {
        if(value != null) {
            if (value.isNumber()) {
                mView.setProgress(value.asInt());
            } else if (value.isString()) {
                mProgress = value;
                if(hTemplate != null) {
                    try {
                        String progressString = apply(hTemplate, value.asString());
                        mView.setProgress((int) Double.parseDouble(progressString));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void setMax(JsonValue value) {
        if(value != null) {
            if (value.isNumber()) {
                mView.setMax(value.asInt());
            } else if (value.isString()) {
                mMax = value;
                if(hTemplate != null) {
                    try {
                        String maxSring = apply(hTemplate, value.asString());
                        mView.setMax((int) Double.parseDouble(maxSring));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    @Override
    public void onAdapterGetView(int position, JsonObject positionData, HTemplate privateHTemplate) {
        super.onAdapterGetView(position, positionData, privateHTemplate);
        hTemplate.asAll(privateHTemplate);
        setProgress(mProgress);
        setMax(mMax);
    }

    @Override
    public void onMeasure(int parentWidth, int parentHeight) {
        Log.i(TAG, "parentWidth="+parentWidth+", parentHeight="+parentHeight);
        super.onMeasure(parentWidth, parentHeight);
    }
}
