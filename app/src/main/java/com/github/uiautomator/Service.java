package com.github.uiautomator;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Service extends android.app.Service {
    public static final String ACTION_START = "com.github.uiautomator.ACTION_START";
    public static final String ACTION_STOP = "com.github.uiautomator.ACTION_STOP";

    private static final String TAG = "ATXService";
    private static final int NOTIFICATION_ID = 0x1;

    WifiManager.WifiLock mWifiLock = null;

    public Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't support binding to this service
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setTicker(getString(R.string.service_ticker))
                .setContentTitle(getString(R.string.service_title))
                .setContentText(getString(R.string.service_text))
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setWhen(System.currentTimeMillis())
                .build();

        startForeground(NOTIFICATION_ID, notification);
        holdWifiLock();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Stopping service");
        stopForeground(true);
        releaseWifiLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();

        if (ACTION_START.equals(action)) {
            Log.i(TAG, "Receive start-service action, but ignore it");
        } else if (ACTION_STOP.equals(action)) {
            stopSelf();
        } else {
            Log.e(TAG, "Unknown action " + action);
        }

        return START_STICKY;
    }

    @Override
    public void onLowMemory() {
        Log.w(TAG, "Low memory");
    }

    /***
     * Calling this method will aquire the lock on wifi. This is avoid wifi
     * from going to sleep as long as <code>releaseWifiLock</code> method is called.
     **/
    private void holdWifiLock() {
        Log.i(TAG, "Hold wifi-lock");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (mWifiLock == null)
            mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);

        mWifiLock.setReferenceCounted(false);

        if (!mWifiLock.isHeld())
            mWifiLock.acquire();
    }

    /***
     * Calling this method will release if the lock is already help. After this method is called,
     * the Wifi on the device can goto sleep.
     **/
    private void releaseWifiLock() {
        Log.i(TAG, "Release wifi-lock");
        if (mWifiLock == null)
            Log.w(TAG, "#releaseWifiLock mWifiLock was not created previously");

        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }
}
