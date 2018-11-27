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
//
// Sync to new code: https://android.googlesource.com/platform/frameworks/testing/+/master/uiautomator/library/core-src/com/android/uiautomator/core/AccessibilityNodeInfoDumper.java
class AccessibilityNodeInfoDumper {
    private static final String LOGTAG = AccessibilityNodeInfoDumper.class.getSimpleName();
    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{
            GridView.class.getName(), GridLayout.class.getName(),
            ListView.class.getName(), TableLayout.class.getName()
    };

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

        for (int i$ = 0; i$ < len$; ++i$) {
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

        if (Build.VERSION.SDK_INT >= 21) {
//        if (API_LEVEL_ACTUAL >= 21) {
            Iterator i$ = uiAutomation.getWindows().iterator();

            while (i$.hasNext()) {
                AccessibilityWindowInfo window = (AccessibilityWindowInfo) i$.next();
                AccessibilityNodeInfo root = window.getRoot();
                if (root == null) {
                    Log.w(LOGTAG, String.format("Skipping null root node for window: %s", window.toString()));
                } else {
                    roots.add(root);
                }
            }
        }

        return (AccessibilityNodeInfo[]) roots.toArray(new AccessibilityNodeInfo[roots.size()]);
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

        for (int i = 0; i < count; ++i) {
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

    /**
     * The list of classes to exclude my not be complete. We're attempting to
     * only reduce noise from standard layout classes that may be falsely
     * configured to accept clicks and are also enabled.
     *
     * @param node
     * @return true if node is excluded.
     */
    private static boolean nafExcludedClass(AccessibilityNodeInfo node) {
        String className = safeCharSeqToString(node.getClassName());
        for (String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName))
                return true;
        }
        return false;
    }

    /**
     * We're looking for UI controls that are enabled, clickable but have no
     * text nor content-description. Such controls configuration indicate an
     * interactive control is present in the UI and is most likely not
     * accessibility friendly. We refer to such controls here as NAF controls
     * (Not Accessibility Friendly)
     *
     * @param node
     * @return false if a node fails the check, true if all is OK
     */
    private static boolean nafCheck(AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled()
                && safeCharSeqToString(node.getContentDescription()).isEmpty()
                && safeCharSeqToString(node.getText()).isEmpty();
        if (!isNaf)
            return true;
        // check children since sometimes the containing element is clickable
        // and NAF but a child's text or description is available. Will assume
        // such layout as fine.
        return childNafCheck(node);
    }


    /**
     * This should be used when it's already determined that the node is NAF and
     * a further check of its children is in order. A node maybe a container
     * such as LinerLayout and may be set to be clickable but have no text or
     * content description but it is counting on one of its children to fulfill
     * the requirement for being accessibility friendly by having one or more of
     * its children fill the text or content-description. Such a combination is
     * considered by this dumper as acceptable for accessibility.
     *
     * @param node
     * @return false if node fails the check.
     */
    private static boolean childNafCheck(AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (childNode == null) {
                Log.i(LOGTAG, String.format("Null child %d/%d, parent: %s",
                        x, childCount, node.toString()));
                continue;
            }
            if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty()
                    || !safeCharSeqToString(childNode.getText()).isEmpty())
                return true;
            if (childNafCheck(childNode))
                return true;
        }
        return false;
    }


    private static String safeCharSeqToString(CharSequence cs) {
        return cs == null ? "" : stripInvalidXMLChars(cs);
    }

    private static String stripInvalidXMLChars(CharSequence cs) {
        // ref: https://stackoverflow.com/questions/4237625/removing-invalid-xml-characters-from-a-string-in-java
        String xml10pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";

        return cs.toString().replaceAll(xml10pattern, "?");
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
