package com.github.uiautomator.stub;

import android.app.UiAutomation;
import android.graphics.Rect;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.util.Xml;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableLayout;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


// Note:
// Here is a copy of android.support.test.uiautomator.AccessibilitiNodeInfoDumper source code
// in order to fix dump hierarchy error
class AccessibilityNodeInfoDumper {
    private static final String LOGTAG = AccessibilityNodeInfoDumper.class.getSimpleName();
    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{GridView.class.getName(), GridLayout.class.getName(), ListView.class.getName(), TableLayout.class.getName()};

    AccessibilityNodeInfoDumper() {
    }

    public static void dumpWindowHierarchy(UiDevice device, OutputStream out) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.setOutput(out, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "hierarchy");
        serializer.attribute("", "rotation", Integer.toString(device.getDisplayRotation()));
        AccessibilityNodeInfo[] arr$ = getWindowRoots(device); // device.getWindowRoots();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            AccessibilityNodeInfo root = arr$[i$];
            dumpNodeRec(root, serializer, 0, device.getDisplayWidth(), device.getDisplayHeight());
        }

        serializer.endTag("", "hierarchy");
        serializer.endDocument();
    }

    private static AccessibilityNodeInfo[] getWindowRoots(UiDevice device) {
        device.waitForIdle();
        Set<AccessibilityNodeInfo> roots = new HashSet();
        UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
        AccessibilityNodeInfo activeRoot = uiAutomation.getRootInActiveWindow();
        if (activeRoot != null) {
            roots.add(activeRoot);
        }

        if (Build.VERSION.SDK_INT >= 21){
//        if (API_LEVEL_ACTUAL >= 21) {
            Iterator i$ = uiAutomation.getWindows().iterator();

            while(i$.hasNext()) {
                AccessibilityWindowInfo window = (AccessibilityWindowInfo)i$.next();
                AccessibilityNodeInfo root = window.getRoot();
                if (root == null) {
                    Log.w(LOGTAG, String.format("Skipping null root node for window: %s", window.toString()));
                } else {
                    roots.add(root);
                }
            }
        }

        return (AccessibilityNodeInfo[])roots.toArray(new AccessibilityNodeInfo[roots.size()]);
    }

    private static void dumpNodeRec(AccessibilityNodeInfo node, XmlSerializer serializer, int index, int width, int height) throws IOException {
        serializer.startTag("", "node");
        if (!nafExcludedClass(node) && !nafCheck(node)) {
            serializer.attribute("", "NAF", Boolean.toString(true));
        }

        serializer.attribute("", "index", Integer.toString(index));
        serializer.attribute("", "text", safeCharSeqToString(node.getText()));
        serializer.attribute("", "resource-id", safeCharSeqToString(node.getViewIdResourceName()));
        serializer.attribute("", "class", safeCharSeqToString(node.getClassName()));
        serializer.attribute("", "package", safeCharSeqToString(node.getPackageName()));
        serializer.attribute("", "content-desc", safeCharSeqToString(node.getContentDescription()));
        serializer.attribute("", "checkable", Boolean.toString(node.isCheckable()));
        serializer.attribute("", "checked", Boolean.toString(node.isChecked()));
        serializer.attribute("", "clickable", Boolean.toString(node.isClickable()));
        serializer.attribute("", "enabled", Boolean.toString(node.isEnabled()));
        serializer.attribute("", "focusable", Boolean.toString(node.isFocusable()));
        serializer.attribute("", "focused", Boolean.toString(node.isFocused()));
        serializer.attribute("", "scrollable", Boolean.toString(node.isScrollable()));
        serializer.attribute("", "long-clickable", Boolean.toString(node.isLongClickable()));
        serializer.attribute("", "password", Boolean.toString(node.isPassword()));
        serializer.attribute("", "selected", Boolean.toString(node.isSelected()));
        serializer.attribute("", "visible-to-user", Boolean.toString(node.isVisibleToUser()));
        serializer.attribute("", "bounds", getVisibleBoundsInScreen(node, width, height).toShortString());
        int count = node.getChildCount();

        for(int i = 0; i < count; ++i) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.isVisibleToUser()) {
                    dumpNodeRec(child, serializer, i, width, height);
                    child.recycle();
                } else {
                    android.util.Log.i(LOGTAG, String.format("Skipping invisible child: %s", child.toString()));
                }
            } else {
                Log.i(LOGTAG, String.format("Null child %d/%d, parent: %s", i, count, node.toString()));
            }
        }

        serializer.endTag("", "node");
    }

    private static boolean nafExcludedClass(AccessibilityNodeInfo node) {
        String className = safeCharSeqToString(node.getClassName());
        String[] arr$ = NAF_EXCLUDED_CLASSES;
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String excludedClassName = arr$[i$];
            if (className.endsWith(excludedClassName)) {
                return true;
            }
        }

        return false;
    }

    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled() && safeCharSeqToString(node.getContentDescription()).isEmpty() && safeCharSeqToString(node.getText()).isEmpty();
        return !isNaf ? true : childNafCheck(node);
    }

    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();

        for(int x = 0; x < childCount; ++x) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty() || !safeCharSeqToString(childNode.getText()).isEmpty()) {
                return true;
            }

            if (childNafCheck(childNode)) {
                return true;
            }
        }

        return false;
    }

    private static String safeCharSeqToString(CharSequence cs) {
        return cs == null ? "" : stripInvalidXMLChars(cs);
    }

    private static String stripInvalidXMLChars(CharSequence cs) {
        StringBuffer ret = new StringBuffer();

        for(int i = 0; i < cs.length(); ++i) {
            char ch = cs.charAt(i);
            if (ch >= 1 && ch <= '\b' || ch >= 11 && ch <= '\f' || ch >= 14 && ch <= 31 || ch >= 127 && ch <= 132 || ch >= 134 && ch <= 159 || ch >= '\ufdd0' && ch <= '\ufddf' || ch >= 131070 && ch <= 131071 || ch >= 196606 && ch <= 196607 || ch >= 262142 && ch <= 262143 || ch >= 327678 && ch <= 327679 || ch >= 393214 && ch <= 393215 || ch >= 458750 && ch <= 458751 || ch >= 524286 && ch <= 524287 || ch >= 589822 && ch <= 589823 || ch >= 655358 && ch <= 655359 || ch >= 720894 && ch <= 720895 || ch >= 786430 && ch <= 786431 || ch >= 851966 && ch <= 851967 || ch >= 917502 && ch <= 917503 || ch >= 983038 && ch <= 983039 || ch >= 1048574 && ch <= 1048575 || ch >= 1114110 && ch <= 1114111) {
                ret.append(".");
            } else {
                ret.append(ch);
            }
        }

        return ret.toString();
    }

    static android.graphics.Rect getVisibleBoundsInScreen(AccessibilityNodeInfo node, int width, int height) {
        if (node == null) {
            return null;
        } else {
            android.graphics.Rect nodeRect = new android.graphics.Rect();
            node.getBoundsInScreen(nodeRect);
            android.graphics.Rect displayRect = new android.graphics.Rect();
            displayRect.top = 0;
            displayRect.left = 0;
            displayRect.right = width;
            displayRect.bottom = height;
            nodeRect.intersect(displayRect);
            if (Build.VERSION.SDK_INT >= 21) {  //  UiDevice.API_LEVEL_ACTUAL
                android.graphics.Rect window = new Rect();
                if (node.getWindow() != null) {
                    node.getWindow().getBoundsInScreen(window);
                    nodeRect.intersect(window);
                }
            }

            return nodeRect;
        }
    }
}
