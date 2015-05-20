package com.github.uiautomator.stub;

import android.os.Build;

/**
 * Created with IntelliJ IDEA.
 * User: b036
 * Date: 8/13/13
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class NotImplementedException extends Exception {
    public NotImplementedException() {
        super("This method is not yet implemented in API level " + Build.VERSION.SDK_INT + ".");
    }

    public NotImplementedException(String method) {
        super(method + " is not yet implemented in API level " + Build.VERSION.SDK_INT + ".");
    }
}
