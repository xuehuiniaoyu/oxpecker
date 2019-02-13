package xuehuiniaoyu.github.oxpecker.activity;

import android.os.Bundle;

import org.ny.woods.app.HActivity;

import xuehuiniaoyu.github.oxpecker.R;

public class ConstantActivity extends HActivity {
    public static class Obj {
        private String arg;
        private int count;

        public Obj(String arg, int count) {
            this.arg = arg;
            this.count = count;
        }

        public String getArg() {
            return arg;
        }

        public int getCount() {
            return count;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHTemplate().as("name1", "自定义变量1");
        getHTemplate().as("name2", getString(R.string.app_name));
        getHTemplate().as("obj", new Obj("Value from Activity", 1));
        setContentViewFromAssets("constant.hj");
    }
}
