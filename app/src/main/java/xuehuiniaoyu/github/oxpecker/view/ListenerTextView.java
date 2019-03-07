package xuehuiniaoyu.github.oxpecker.view;

import android.content.Context;

import org.hjson.JsonValue;
import org.ny.woods.layout.widget.HTextView;
import org.ny.woods.os.Message;

/**
 *
 * 监听内容变化显示的TextView
 */
public class ListenerTextView extends HTextView {
    public ListenerTextView(Context context, JsonValue value) {
        super(context, value);
    }

    @Override
    public void onHandleMsg(Message msg) {
        // 获取到消息
        if(msg.getWhat() == 0) {
            mView.setText(msg.getObj());
        }
    }
}
