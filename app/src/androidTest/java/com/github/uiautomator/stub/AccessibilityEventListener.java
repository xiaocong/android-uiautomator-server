package com.github.uiautomator.stub;

import android.app.UiAutomation;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Used to skip apk auto install && permission popups
 * Called in method: setPermissionPatterns
 * <p>
 * Created by hzsunshx on 2018/3/7.
 */

public class AccessibilityEventListener implements UiAutomation.OnAccessibilityEventListener {
    private UiDevice device;

    public AccessibilityEventListener(UiDevice device) {
        this.device = device;
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            return;
        }
        if ((event.getEventType() & (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) != 0) {
            device.runWatchers();
        }
    }

    private boolean performInstallation(AccessibilityEvent event, String[] texts) {
        List<AccessibilityNodeInfo> nodeInfoList;
        for (String text : texts) {
            nodeInfoList = event.getSource().findAccessibilityNodeInfosByText(text);
            // Note: findAccessibilityNodeInfosByText will return all node which contains text
            if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//                boolean performed = performClick(nodeInfoList, text, Button.class.getName());
//                if (performed) return true;
            }
        }
        return false;
    }

    private boolean performClick(List<AccessibilityNodeInfo> nodeInfoList, String text, String widgetType) {
        for (AccessibilityNodeInfo node : nodeInfoList) {
            Log.d("travel node: " + node);
            if (node.isClickable() && node.isEnabled() && text.equals(node.getText())) {
                if (widgetType == null || node.getClassName().equals(widgetType)) {
                    Log.d("click: " + node.getText());
                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }
}
