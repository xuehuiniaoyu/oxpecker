package xuehuiniaoyu.github.oxpecker.activity;

import android.os.Bundle;

import org.ny.woods.app.HActivity;

import java.io.File;

public class ImgShowActivity extends HActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String src = getIntent().getStringExtra("src");
        getHTemplate().as("img-name", new File(src).getName());
        setContentViewFromAssets("img_show.hj", false);
    }
}
