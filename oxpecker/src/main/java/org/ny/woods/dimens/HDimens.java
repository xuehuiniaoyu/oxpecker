package org.ny.woods.dimens;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.hjson.JsonValue;
import org.ny.woods.exception.HException;
import org.ny.woods.utils.StringUtil;

/**
 * 尺寸工具接口类
 */
public abstract class HDimens {

    public static final int AUTO = 0;
    public static final int X = 1;
    public static final int Y = 2;

    public static class SizeInfo {

        public static final int SIZE_TYPE_UNKNOWN = -1;
        public static final int SIZE_TYPE_NUMBER = 1;
        public static final int SIZE_TYPE_PX = 2;
        public static final int SIZE_TYPE_DP = 3;
        public static final int SIZE_TYPE_SP = 4;
        public static final int SIZE_TYPE_PERCENT = 5;
        public static final int SIZE_TYPE_AUTO = 6;

        final static SizeInfo UNKNOWN = new SizeInfo(ViewGroup.LayoutParams.WRAP_CONTENT, SIZE_TYPE_UNKNOWN);

        float size;
        int sizeType;
        int finalSizeType;

        public SizeInfo(float size, int sizeType) {
            this.size = size;
            this.finalSizeType = this.sizeType = sizeType;
        }

        public float getSize() {
            return size;
        }

        public int getSizeType() {
            return sizeType;
        }

        public final int getFinalSizeType() {
            return finalSizeType;
        }

        SizeInfo setSize(float size) {
            this.size = size;
            return this;
        }

        SizeInfo setSizeType(int sizeType) {
            this.sizeType = sizeType;
            return this;
        }
    }


    //
    int parentWidth;
    int parentHeight;

    /**
     *
     * 锁定，锁定后 parentWidth和parentHeight不能被更改
     */
    private boolean locked;

    protected int width = 1024;
    protected int height = 720;

    protected float widthScale;
    protected float heightScale;

    public DisplayMetrics displayMetrics;

    public HDimens() {
    }

    public HDimens(HDimens dimens) {
        this.displayMetrics = dimens.displayMetrics;
        set(dimens.getParentWidth(), dimens.getParentHeight()).scaleTo(dimens.width, dimens.height).ok();
    }

    public abstract HDimens newHDimens();

    /**
     * 指定父容器宽高
     *
     * @param parentWidth
     * @param parentHeight
     * @return
     */
    public HDimens set(int parentWidth, int parentHeight) {
        if(!locked) {
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
        }
        return this;
    }

    public void setParentWidth(int parentWidth) {
        if(!locked) {
            this.parentWidth = parentWidth;
        }
    }

    public void setParentHeight(int parentHeight) {
        if(!locked) {
            this.parentHeight = parentHeight;
        }
    }

    /**
     * 修改默认尺寸
     * 用户可以跟据设置的width值和height值计算出最终结果
     *
     * @param width
     * @param height
     * @see #getWidth(JsonValue value)
     * @see #getHeight(JsonValue value)
     */
    public HDimens scaleTo(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * 修改完成
     *
     * @return
     */
    public HDimens ok() {
        widthScale = parentWidth * 1.0F / this.width;
        heightScale = parentHeight * 1.0F / this.height;
        Log.i("TAG", "width:" + width + " widthScale:" + widthScale);
        Log.i("TAG", "height:" + height + " heightScale:" + widthScale);
        return this;
    }

    /**
     * 根据设置尺寸返回最终尺寸
     *
     * @param value
     * @return
     */
    public abstract SizeInfo getWidth(JsonValue value);

    /**
     * 根据设置尺寸返回最终尺寸
     *
     * @param value
     * @return
     */
    public abstract SizeInfo getHeight(JsonValue value);

    public SizeInfo getPxSize(JsonValue value, int o) {
        if(value.isNumber()) {
            return new SizeInfo(value.asFloat(), SizeInfo.SIZE_TYPE_NUMBER);
        }
        else {
            String valueString = value.asString();
            if("auto".equals(valueString)) {
                if(o == AUTO) {
                    throw new HException("Type error!");
                }
                return new SizeInfo(RelativeLayout.LayoutParams.WRAP_CONTENT, SizeInfo.SIZE_TYPE_AUTO);
            }
            else if("fill".equals(valueString)) {
                switch (o) {
                    case X:
                        return new SizeInfo(width, SizeInfo.SIZE_TYPE_NUMBER);
                    case Y:
                        return new SizeInfo(height, SizeInfo.SIZE_TYPE_NUMBER);
                    default:
                        throw new HException("Type error!");
                }
            }
            else if(valueString.contains("%")) {
                return new SizeInfo(StringUtil.getNumberFromString(valueString), SizeInfo.SIZE_TYPE_PERCENT);
            }
            else if(valueString.contains("dp") || valueString.contains("dip")) {
                float floatValue = StringUtil.getNumberFromString(valueString);
                if(o == X) {
                    return new SizeInfo(floatValue * (displayMetrics.xdpi/160.0F), SizeInfo.SIZE_TYPE_DP);
                }
                else if(o == Y) {
                    return new SizeInfo(floatValue * (displayMetrics.ydpi/160.0F), SizeInfo.SIZE_TYPE_DP);
                }
                return new SizeInfo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, floatValue, displayMetrics), SizeInfo.SIZE_TYPE_DP);
            }
            else if(valueString.contains("px")) {
                float floatValue = StringUtil.getNumberFromString(valueString);
                return new SizeInfo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, floatValue, displayMetrics), SizeInfo.SIZE_TYPE_PX);
            }
            else if(valueString.contains("sp")) {
                float floatValue = StringUtil.getNumberFromString(valueString);
                return new SizeInfo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, floatValue, displayMetrics), SizeInfo.SIZE_TYPE_SP);
            }
            return new SizeInfo(StringUtil.getNumberFromString(valueString), SizeInfo.SIZE_TYPE_NUMBER).setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
        }
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    //    /**
//     * 获取最终尺寸
//     *
//     * @param value
//     * @return
//     */
//    public float getSize(JsonValue value) {
//        if (value.isNumber()) {
//            return value.asInt();
//        } else {
//            String valueString = value.asString();
//            float floatValue = StringUtil.getNumberFromString(valueString);
//            if (valueString.contains("sp")) {
//                return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, floatValue, displayMetrics);
//            } else if (valueString.contains("dp") || valueString.contains("dip")) {
//                return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, floatValue, displayMetrics);
//            } else if (valueString.contains("px")) {
//                return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, floatValue, displayMetrics);
//            }
//        }
//        return UNKNOWN;
//    }

    public float getWidthScale() {
        return widthScale;
    }

    public float getHeightScale() {
        return heightScale;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getParentWidth() {
        return parentWidth;
    }

    public int getParentHeight() {
        return parentHeight;
    }
}
