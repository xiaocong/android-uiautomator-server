package com.github.uiautomator.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by hzsunshx on 2017/11/15.
 */

public class BatteryMonitor extends AbstractMonitor {
    private static final String TAG = "UIABatteryMonitor";

    private BroadcastReceiver receiver = null;

    public BatteryMonitor(Context context, HttpPostNotifier notifier) {
        super(context, notifier);
    }

    @Override
    public void register() {
        Log.i(TAG, "Monitor starting");

        receiver = new BroadcastReceiver() {
            private int level;

            @Override
            public void onReceive(Context context, Intent intent) {
                report(notifier, intent);
            }
        };
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void unregister() {
        context.unregisterReceiver(receiver);
    }

    private void report(HttpPostNotifier notifier, Intent intent) {
        Integer level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        Log.d(TAG, "notify battery changed. current level " + level);
        notifier.Notify("/info/battery", "");
    }
}
