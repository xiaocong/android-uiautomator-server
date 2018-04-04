package com.github.uiautomator.stub;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Used to skip apk auto install && permission popups
 * Called in method: setPermissionPatterns
 * <p>
 * Created by hzsunshx on 2018/3/7.
 */

public class AccessibilityEventListener implements UiAutomation.OnAccessibilityEventListener {
    //    private String[] buttonTexts; // For example: "确定", "安装", "继续安装", "下一步", "完成"
    private HashMap<String, String[]> patterns;
    private Selector[] selectors;
    private HashMap<String, List<Selector>> selectorMap;
    private Instrumentation mInstrumentation;

    public AccessibilityEventListener(HashMap<String, String[]> patterns) {
        this.patterns = patterns;

        // this.selectors = selectors;
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event.getPackageName() == null) {
            return;
        }
        CharSequence eventPackageName = event.getPackageName();
        for (String packageName : this.patterns.keySet()){
            if (packageName.equals(eventPackageName)){
                String[] labels = patterns.get(eventPackageName);
                boolean r = performInstallation(event, labels);
                Log.d("Action perform: "+ TextUtils.join(", ", labels) + " " + r);
            }
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
