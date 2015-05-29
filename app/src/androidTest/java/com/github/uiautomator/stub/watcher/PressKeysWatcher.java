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

import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.view.KeyEvent;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;
import com.github.uiautomator.stub.Log;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocong@gmail.com
 * Date: 8/21/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class PressKeysWatcher extends SelectorWatcher{
    private String[] keys = new String[]{};
    private UiDevice device = null;

    public PressKeysWatcher(UiSelector[] conditions, String[] keys) {
        super(conditions);
        this.keys = keys;
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Override
    public void action() {
        Log.d("PressKeysWatcher triggered!");
        for (String key: keys) {
            key = key.toLowerCase();
            if ("home".equals(key))
                device.pressHome();
            else if ("back".equals(key))
                device.pressBack();
            else if ("left".equals(key))
                device.pressDPadLeft();
            else if ("right".equals(key))
                device.pressDPadRight();
            else if ("up".equals(key))
                device.pressDPadUp();
            else if ("down".equals(key))
                device.pressDPadDown();
            else if ("center".equals(key))
                device.pressDPadCenter();
            else if ("menu".equals(key))
                device.pressMenu();
            else if ("search".equals(key))
                device.pressSearch();
            else if ("enter".equals(key))
                device.pressEnter();
            else if ("delete".equals(key) || "del".equals(key))
                device.pressDelete();
            else if ("recent".equals(key))
                try {
                    device.pressRecentApps();
                } catch (RemoteException e) {
                    Log.d(e.getMessage());
                }
            else if ("volume_up".equals(key))
                device.pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
            else if ("volume_down".equals(key))
                device.pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
            else if ("volume_mute".equals(key))
                device.pressKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);
            else if ("camera".equals(key))
                device.pressKeyCode(KeyEvent.KEYCODE_CAMERA);
            else if ("power".equals(key))
                device.pressKeyCode(KeyEvent.KEYCODE_POWER);
        }
    }
}
