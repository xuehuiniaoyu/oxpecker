package org.ny.woods.layout.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.hjson.JsonValue;
import org.ny.woods.annotations.Extension;
import org.ny.woods.utils.HUri;

import glide.GlideApp;
import glide.GlideRequest;
import glide.GlideRequests;

public class HImageView extends HView<ImageView> {
    private JsonValue type;
    private JsonValue src;

    private int placeholder = -1;
    private int error = -1;

    // 淡入效果
    private boolean fade;
    // 动画过度时间
    private long duration = 800;

    public HImageView(Context context, JsonValue value) {
        super(context, value);
    }

    public void setType(JsonValue value) {
        this.type = value;
    }

    /**
     * 设置图片
     * <p>
     * 你的布局文件配置可以这么配
     * assets    src:"file:///android_asset/f003.gif"
     * http      src:"http://www.xxxx.com/img.jpg"
     * raw       src:"android.resource://{{package}}/raw/test"
     * drawable  src:"android.resource://{{package}}/drawable/test"
     * <p>
     * sdcard    src:"file://{{SD}}/test"
     * <p>
     * placeholder和error必须是drawable下的资源
     *
     * @param value
     */
    public void setSrc(JsonValue value) {
        this.src = value;
    }

    @Override
    public void onLayout() {
        super.onLayout();
        if (src != null) {
            String valueString = src.asString();
            if(HUri.isUri(valueString)) {
                GlideRequests requests;
                GlideRequest request = (requests = GlideApp.with(getContext()))
                        .load(valueString);
                if(placeholder != -1) {
                    request.placeholder(placeholder);
                }
                if(error != -1) {
                    request.error(error);
                }
                request.centerInside();
                if (type != null) {
                    if (requests != null) {
                        switch (type.asString()) {
                            case "gif": {
                                requests.asGif();
                                break;
                            }
                            case "bitmap": {
                                requests.asBitmap();
                                break;
                            }
                            case "drawable": {
                                requests.asDrawable();
                                break;
                            }
                        }
                    }
                }
                if(fade && duration > 0) {
                    request.addListener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( mView, "alpha", 0.0F, 1.0F );
                                fadeAnim.setDuration( duration );
                                fadeAnim.start();
                            }
                            return false;
                        }
                    });
                }
                if (request != null) {
                    request.into(mView);
                }
            }
            else {
                mView.setImageResource(getResourceId(valueString));
            }
        }
    }

    /**
     * 设置填充方式
     *
     * @param value
     */
    public void setScaleType(JsonValue value) {
        switch (value.asString()) {
            case "fit_xy": {
                mView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            }
            case "center": {
                mView.setScaleType(ImageView.ScaleType.CENTER);
                break;
            }
            case "fit_start": {
                mView.setScaleType(ImageView.ScaleType.FIT_START);
                break;
            }
            case "fit_center": {
                mView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            }
            case "fit_end": {
                mView.setScaleType(ImageView.ScaleType.FIT_END);
                break;
            }
            case "matrix": {
                mView.setScaleType(ImageView.ScaleType.MATRIX);
                break;
            }
        }
    }

    @Extension("default")
    /**
     * 设置初始化占位图
     */
    public void setPlaceholder(JsonValue value) {
        this.placeholder = getResourceId(value.asString());
    }

    /**
     * 设置图片下载失败的占位图
     * @param value
     */
    public void setError(JsonValue value) {
        this.error = getResourceId(value.asString());
    }

    /**
     * 设置淡入淡出
     * @param value
     */
    public void setFade(JsonValue value) {
        if((this.fade = value.asBoolean())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mView.setAlpha(0.0F);
            }
        }
    }

    /**
     * 设置动画过渡时间
     * @param value
     */
    public void setDuration(JsonValue value) {
        this.duration = value.asLong();
    }
}
