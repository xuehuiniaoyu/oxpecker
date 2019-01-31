package org.ny.woods.interceptor.i;

import android.content.Context;

import org.hjson.JsonObject;
import org.ny.woods.dimens.HDimens;

public interface HInterceptor<T> {
    T onInterceptor(Context context, HDimens dimens, JsonObject jsonObject);
}
