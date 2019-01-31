package org.ny.woods.dimens;

import org.hjson.JsonValue;
import org.ny.woods.utils.StringUtil;

/**
 * 百分比
 */
public class PercentDimens extends RelativeDimens {
    public PercentDimens() {
        super();
    }

    public PercentDimens(HDimens dimens) {
        super(dimens);
    }

    @Override
    public HDimens newHDimens() {
        return new PercentDimens(this);
    }

    @Override
    public SizeInfo getWidth(JsonValue value) {
        SizeInfo result = super.getWidth(value);
        if (result.getSizeType() == SizeInfo.SIZE_TYPE_UNKNOWN) {
            if (result.getFinalSizeType() == SizeInfo.SIZE_TYPE_PERCENT) {
                String valueString = value.asString();
                if ("%h".equals(valueString)) {
                    return getHeight(value);
                } else {
                    return result.setSize((int) (result.getSize() / 100.0F * width * widthScale + 0.5F));
                }
            }
        }
        return result.setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
    }

    @Override
    public SizeInfo getHeight(JsonValue value) {
        SizeInfo result = super.getHeight(value);
        if (result.getSizeType() == SizeInfo.SIZE_TYPE_UNKNOWN) {
            if (result.getFinalSizeType() == SizeInfo.SIZE_TYPE_PERCENT) {
                String valueString = value.asString();
                if ("%w".equals(StringUtil.matchString(valueString))) {
                    return getWidth(value);
                } else {
                    return result.setSize((int) (result.getSize() / 100.0F * height * heightScale + 0.5F));
                }
            }
        }
        return result.setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
    }
}
