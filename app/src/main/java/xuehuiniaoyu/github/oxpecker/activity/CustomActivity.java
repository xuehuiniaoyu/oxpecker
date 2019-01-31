package xuehuiniaoyu.github.oxpecker.activity;

import android.os.Bundle;

import org.ny.woods.app.HActivity;

public class CustomActivity extends HActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewFromAssets(getIntent().getStringExtra("layout"));
    }

}
