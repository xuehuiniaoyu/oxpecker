package xuehuiniaoyu.github.oxpecker.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hjson.JsonValue;
import org.ny.woods.layout.widget.base.GroupWapper;

import xuehuiniaoyu.github.oxpecker.R;


public class MyLayout extends GroupWapper<LinearLayout> {
    private TextView text1, text2;
    public MyLayout(Context context, JsonValue value) {
        super(context, value);
        View.inflate(context, R.layout.my_layout, mView);
        text1 = mView.findViewById(R.id.text1);
        text2 = mView.findViewById(R.id.text2);
    }

    public void setText1(JsonValue value) {
        text1.setText(value.asString());
    }

    public void setText2(JsonValue value) {
        text2.setText(value.asString());
    }
}
