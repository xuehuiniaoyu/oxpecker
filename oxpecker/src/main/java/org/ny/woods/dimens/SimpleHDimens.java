package org.ny.woods.dimens;

import org.hjson.JsonValue;

/**
 * 默认工具，不做任何计算
 */
public class SimpleHDimens extends HDimens {

    public SimpleHDimens() {
        super();
    }

    public SimpleHDimens(HDimens dimens) {
        super(dimens);
    }

    @Override
    public HDimens newHDimens() {
        return new SimpleHDimens(this);
    }

    @Override
    public SizeInfo getWidth(JsonValue value) {
        SizeInfo result = getPxSize(value, X);
        switch (result.getFinalSizeType()) {
            case SizeInfo.SIZE_TYPE_AUTO:
            case SizeInfo.SIZE_TYPE_DP:
            case SizeInfo.SIZE_TYPE_SP:
            case SizeInfo.SIZE_TYPE_PX:
                return result;
        }
        return result.setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
    }

    @Override
    public SizeInfo getHeight(JsonValue value) {
        SizeInfo result = getPxSize(value, Y);
        switch (result.getSizeType()) {
            case SizeInfo.SIZE_TYPE_AUTO:
            case SizeInfo.SIZE_TYPE_DP:
            case SizeInfo.SIZE_TYPE_SP:
            case SizeInfo.SIZE_TYPE_PX:
                return result;
        }
        return result.setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
    }
}
