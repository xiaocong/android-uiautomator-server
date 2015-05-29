/*
 * The MIT License (MIT)
 * Copyright (c) 2015 xiaocong@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
