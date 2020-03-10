package com.github.uiautomator.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by hzsunshx on 2017/11/15.
 * Deprecated, use RotationAgent to watch rotation.
 */

public class RotationMonitor extends AbstractMonitor {
    private static final String TAG = "UIARotationMonitor";

    BroadcastReceiver receiver;
    WindowManager windowService;

    public RotationMonitor(Context context, HttpPostNotifier notifier) {
        super(context, notifier);
    }

    @Override
    public void init() {
        Log.i(TAG, "Rotation monitor init");
        this.windowService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                report();
            }
        };
    }

    @Override
    public void register() {
        Log.i(TAG, "Rotation monitor starting");
        report(); // need to notify for the first time

        // FIXME(ssx): when change from 90 degree to 270 degree. no broadcast received
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
    }

    @Override
    public void unregister() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

    private void report() {
        int rotation = windowService.getDefaultDisplay().getRotation();
        Log.i(TAG, "Orientation " + rotation);
        notifier.Notify("/info/rotation", "" + rotation);
    }
}
