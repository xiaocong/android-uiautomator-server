package com.github.uiautomator.monitor;

import android.content.Context;

/**
 * Created by hzsunshx on 2017/11/15.
 */

abstract public class AbstractMonitor {
    Context context;
    HttpPostNotifier notifier;

    public AbstractMonitor(Context context, HttpPostNotifier notifier) {
        this.context = context;
        this.notifier = notifier;

        this.register();
    }

    abstract public void register();

    abstract public void unregister();
}
