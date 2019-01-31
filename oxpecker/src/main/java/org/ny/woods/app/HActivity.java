package org.ny.woods.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.HTemplate;
import org.ny.woods.template.SimpleHTemplate;
import org.ny.woods.utils.FixMemLeak4Hw;
import org.ny.woods.utils.IDUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HActivity extends Activity {
    private Oxpecker oxpecker;
    private HTemplate hTemplate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hTemplate = new SimpleHTemplate();
        oxpecker = new Oxpecker(this, hTemplate);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        oxpecker.startPecking();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0.0F, 1.0F );
            fadeAnim.setDuration( 600 );
            fadeAnim.start();
        }
    }

    /**
     * 映射对象到为一个别名
     * 注意：这一步必须在 setContentView 调用前完成，否则不生效
     * @param key 别名
     * @param value 被映射对象
     */
    protected void as(String key, Object value) {
        hTemplate.as(key, value);
    }


    /**
     * 从Assets加载模板
     * @param templateName xxx.hjson
     */
    public void setContentViewFromAssets(String templateName) {
        try {
            setContentView(oxpecker.parse(getAssets().open(templateName)).getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从File加载模板
     * @param file
     */
    public void setContentViewFromFile(String file) {
        setContentViewFromFile(new File(file));
    }


    /**
     * 从File加载模板
     * @param file
     */
    public void setContentViewFromFile(File file) {
        try {
            setContentView(oxpecker.parse(new FileInputStream(file)).getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从Raw加载模板
     * @param template
     */
    public void setContentViewFromRaw(int template) {
        try {
            setContentView(oxpecker.parse(getResources().openRawResource(template)).getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载模板
     * @param template
     */
    public void setContentView(String template) {
        try {
            setContentView(oxpecker.parse(template).getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends View> T findViewById(String id) {
        return (T) findViewById(IDUtil.id(id));
    }

    protected HTemplate getHTemplate() {
        return hTemplate;
    }


    /**
     * 通过名称启动Activity
     * @param className
     */
    public void startActivity(String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, className));
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        oxpecker.finishPecking();
        FixMemLeak4Hw.fixLeak(this);
    }
}
