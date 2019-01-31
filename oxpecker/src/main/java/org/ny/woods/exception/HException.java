package org.ny.woods.exception;

import android.util.Log;

public class HException extends UnsupportedOperationException {
    public HException(String message) {
        super(message);
        Log.e(HException.class.getSimpleName(), message);
    }
}
