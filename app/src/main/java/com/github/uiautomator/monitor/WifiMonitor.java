package com.github.uiautomator.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.github.uiautomator.R;
import com.github.uiautomator.Service;

/**
 * Created by hzsunshx on 2018/1/16.
 */

public class WifiMonitor extends AbstractMonitor {
    private static final String TAG = "WifiMonitor";
    private BroadcastReceiver receiver;

    public WifiMonitor(Context context, HttpPostNotifier notifier) {
        super(context, notifier);
    }

    @Override
    public void register() {
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int ip = wifiManager.getConnectionInfo().getIpAddress();
                String ipStr = (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
                ((Service) context).setNotificationContentText(context.getString(R.string.service_text) + " on " + ipStr);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(receiver, filter);
    }

    @Override
    public void unregister() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }
}
