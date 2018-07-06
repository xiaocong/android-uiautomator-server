package com.github.uiautomator.stub;

import android.app.Notification;
import android.app.UiAutomation;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;

/**
 * Used to skip apk auto install && permission popups
 * Called in method: setPermissionPatterns
 * <p>
 * Created by hzsunshx on 2018/3/7.
 */

public class AccessibilityEventListener implements UiAutomation.OnAccessibilityEventListener {
    public String toastMessage;
    public Boolean triggerWatchers = false;
    public long toastTime;

    private HashSet<String> watchers;
    private static AccessibilityEventListener instance;
    private UiDevice device;

    public AccessibilityEventListener(UiDevice device, HashSet<String> watchers) {
        this.device = device;
        this.watchers = watchers;
        AccessibilityEventListener.instance = this;
    }

    public static AccessibilityEventListener getInstance() {
        if (instance == null) {
            throw new RuntimeException(); // Must be init first.
        }
        return instance;
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            return;
        }
        if ((event.getEventType() & (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) != 0) {
            if (triggerWatchers) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (watchers) {
                            device.runWatchers();
                        }
                    }
                }, "Watchers").start();
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable parcelable = event.getParcelableData();
            if (!(parcelable instanceof Notification)) { // without Notification is Toast
                String packageName = event.getPackageName().toString();
                this.toastTime = System.currentTimeMillis();
                toastMessage = "" + event.getText().get(0);
                Log.d("Toast:" + toastMessage + " Pkg:" + packageName + " Time:" + toastTime);
            }
        }
    }
}
