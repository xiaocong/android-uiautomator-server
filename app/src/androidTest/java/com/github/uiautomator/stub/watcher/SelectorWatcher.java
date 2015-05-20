package com.github.uiautomator.stub.watcher;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocong@gmail.com
 * Date: 8/21/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SelectorWatcher implements UiWatcher {
    private UiSelector[] conditions = null;

    public SelectorWatcher(UiSelector[] conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean checkForCondition() {
        for (UiSelector s : conditions) {
            UiObject obj = new UiObject(s);
            if (!obj.exists()) return false;
        }
        action();
        return true;
    }

    public abstract void action();
}
