package org.ny.woods.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.ny.woods.exception.HException;
import org.ny.woods.layout.widget.i.ViewPart;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.HTemplate;
import org.ny.woods.template.SimpleHTemplate;
import org.ny.woods.utils.FixMemLeak4Hw;
import org.ny.woods.utils.GetTask;
import org.ny.woods.utils.IDUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class HActivity extends Activity implements Oxpecker.AsyncTytpe.Callback {

    @Override
    public void onFailed(Exception e, Oxpecker oxpecker) {

    }

    @Override
    public void onSuccess(ViewPart<? extends View> hView, Oxpecker oxpecker) {
        setContentView(hView.getView());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(hView.getView(), "alpha", 0F, 1F);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(600);
            animator.start();
        }
    }

    private Oxpecker oxpecker;
    private SimpleHTemplate hTemplate = new SimpleHTemplate();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getOxpecker().startPecking();
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
    public void setContentViewFromAssets(String templateName, boolean async) {
        InputStream ins;
        try {
            ins = getAssets().open(templateName);
        } catch (IOException e) {
            throw new HException(e.getLocalizedMessage());
        }
        if(async) {
            getOxpecker().inflaterAsync(new Oxpecker.AsyncTytpe(ins, Oxpecker.AsyncTytpe.STREAM), this);
        }
        else {
            setContentView(getOxpecker().parse(ins).getView());
        }
    }


    /**
     * 从File加载模板
     * @param file
     */
    public void setContentViewFromFile(String file, boolean async) {
        setContentViewFromFile(new File(file), async);
    }


    /**
     * 从File加载模板
     * @param file
     */
    public void setContentViewFromFile(File file, boolean async) {
        if(async) {
            getOxpecker().inflaterAsync(new Oxpecker.AsyncTytpe(file, Oxpecker.AsyncTytpe.FILE), this);
        }
        else {
            setContentView(getOxpecker().parse(file).getView());
        }
    }

    /**
     * 从流加载布局
     * @param inputStream
     */
    public void setContentViewFromStream(InputStream inputStream, boolean async) {
        if(async) {
            getOxpecker().inflaterAsync(new Oxpecker.AsyncTytpe(inputStream, Oxpecker.AsyncTytpe.STREAM), this);
        }
        else {
            setContentView(getOxpecker().parse(inputStream).getView());
        }
    }

    /**
     *
     * 从Uri加载布局
     * @param uri
     */
    public void setContentViewFromUri(String uri, boolean async) {
        GetTask getTask = new GetTask(this, uri);
        getTask.setOnResourceLoadListener(new GetTask.OnResourceLoadListener() {
            @Override
            public void onLoadFail(String uri, Exception e) {

            }

            @Override
            public void onLoadSucc(String uri, String resource) {
                setContentView(resource, async);
            }
        }).load();
    }

    /**
     * 从Raw加载模板
     * @param template
     */
    public void setContentViewFromRaw(int template, boolean async) {
        InputStream inputStream = getResources().openRawResource(template);
        setContentViewFromStream(inputStream, async);
    }

    /**
     * 加载模板
     * @param template
     */
    public void setContentView(String template, boolean async) {
        if(async) {
            getOxpecker().inflaterAsync(new Oxpecker.AsyncTytpe(template, Oxpecker.AsyncTytpe.STRING), this);
        }
        else {
            setContentView(getOxpecker().parse(template).getView());
        }
    }

    public <T extends View> T findViewById(String id) {
        return (T) findViewById(IDUtil.id(id));
    }

    protected HTemplate getHTemplate() {
        return hTemplate;
    }

    public Oxpecker getOxpecker() {
        if(oxpecker == null) {
            oxpecker = new Oxpecker(this, hTemplate);
        }
        return oxpecker;
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
        if(oxpecker != null) {
            oxpecker.finishPecking();
            oxpecker = null;
        }
        hTemplate = null;
        FixMemLeak4Hw.fixLeak(this);
    }
}
