package com.github.uiautomator.stub;

import android.app.Notification;
import android.app.UiAutomation;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityEvent;

/**
 * Used to skip apk auto install && permission popups
 * Called in method: setPermissionPatterns
 * <p>
 * Created by hzsunshx on 2018/3/7.
 */

public class AccessibilityEventListener implements UiAutomation.OnAccessibilityEventListener {
    public String toastMessage;
    public Boolean triggerWatchers = false;

    private static AccessibilityEventListener instance;
    private UiDevice device;

    public AccessibilityEventListener(UiDevice device) {
        this.device = device;
    }

    public static AccessibilityEventListener getInstance() {
        if (instance == null) {
            instance = new AccessibilityEventListener(UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()));
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
                device.runWatchers();
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable parcelable = event.getParcelableData();
            if (!(parcelable instanceof Notification)) { // without Notification is Toast
                String packageName = event.getPackageName().toString();
                long eventTime = event.getEventTime();
                toastMessage = (String) event.getText().get(0);
                Log.d("Toast:" + toastMessage + " Pkg:" + packageName + " Time:" + eventTime);
            }
        }
    }

//    private boolean performInstallation(AccessibilityEvent event, String[] texts) {
//        List<AccessibilityNodeInfo> nodeInfoList;
//        for (String text : texts) {
//            nodeInfoList = event.getSource().findAccessibilityNodeInfosByText(text);
//            // Note: findAccessibilityNodeInfosByText will return all node which contains text
//            if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
////                boolean performed = performClick(nodeInfoList, text, Button.class.getName());
////                if (performed) return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean performClick(List<AccessibilityNodeInfo> nodeInfoList, String text, String widgetType) {
//        for (AccessibilityNodeInfo node : nodeInfoList) {
//            Log.d("travel node: " + node);
//            if (node.isClickable() && node.isEnabled() && text.equals(node.getText())) {
//                if (widgetType == null || node.getClassName().equals(widgetType)) {
//                    Log.d("click: " + node.getText());
//                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }
//        }
//        return false;
//    }
}
