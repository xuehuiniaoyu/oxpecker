package xuehuiniaoyu.github.oxpecker.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.ny.woods.app.HActivity;
import org.ny.woods.layout.widget.i.ViewPart;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.SimpleHTemplate;

import java.io.IOException;

import xuehuiniaoyu.github.oxpecker.dialog.FinishDialog;


public class HelloWorldActivity extends HActivity {
    private FinishDialog finishDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentViewFromAssets("hello_world.hj");
        Oxpecker oxpecker = new Oxpecker(this);
        oxpecker.setTemplate(new SimpleHTemplate());
        try {
            oxpecker.inflaterAsync(new Oxpecker.AsyncTytpe(getAssets().open("hello_world.hj"), Oxpecker.AsyncTytpe.STREAM), new Oxpecker.OnPackListener() {
                @Override
                public void onFailed(Exception e, Oxpecker oxpecker) {

                }

                @Override
                public void onSuccess(ViewPart<? extends View> hView, Oxpecker oxpecker) {
                    setContentView(hView.getView());
                    oxpecker.startPecking(); // 必须添加
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(TextView view) {
        if(finishDialog != null) {
            finishDialog.dismiss();
        }
        finishDialog = new FinishDialog(HelloWorldActivity.this, "确定", "取消", "对话框");
        finishDialog.show("弹出对话框！");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(finishDialog != null) {
            finishDialog.dismiss();
            finishDialog = null;
        }
    }
}
