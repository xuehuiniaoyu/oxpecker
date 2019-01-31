package org.ny.woods.dimens;

import org.hjson.JsonValue;
import org.ny.woods.utils.StringUtil;

/**
 * 相对型尺寸工具
 * 宽(width) 相对于 1024
 * 高(height) 相对于 720
 * <p>
 * 自动计算比例
 */
public class RelativeDimens extends SimpleHDimens {

    public RelativeDimens() {
        super();
    }

    public RelativeDimens(HDimens dimens) {
        super(dimens);
    }

    @Override
    public HDimens newHDimens() {
        return new RelativeDimens(this);
    }

    @Override
    public SizeInfo getWidth(JsonValue value) {
        SizeInfo result = super.getWidth(value);
        if (result.getSizeType() == SizeInfo.SIZE_TYPE_UNKNOWN) {
            if (result.getFinalSizeType() == SizeInfo.SIZE_TYPE_NUMBER) {
                if(value.isString()) {
                    String valueString = value.asString();
                    if ("h".equals(StringUtil.matchString(valueString))) {
                        return getHeight(value);
                    }
                }
                return result.setSize(result.getSize() * widthScale);
            }
            return result.setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
        }
        return result;
    }

    @Override
    public SizeInfo getHeight(JsonValue value) {
        SizeInfo result = super.getHeight(value);
        if (result.getSizeType() == SizeInfo.SIZE_TYPE_UNKNOWN) {
            if (result.getFinalSizeType() == SizeInfo.SIZE_TYPE_NUMBER) {
                if(value.isString()) {
                    String valueString = value.asString();
                    if ("w".equals(StringUtil.matchString(valueString))) {
                        return getWidth(value);
                    }
                }
                return result.setSize(result.getSize() * heightScale);
            }
            return result.setSizeType(SizeInfo.SIZE_TYPE_UNKNOWN);
        }
        return result;
    }
}
