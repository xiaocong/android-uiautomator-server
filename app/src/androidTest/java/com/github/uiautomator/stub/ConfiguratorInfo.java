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

package com.github.uiautomator.stub;

import android.support.test.uiautomator.Configurator;

/**
 * Created by xiaocong@gmail.com on 12/26/13.
 */
public class ConfiguratorInfo {

    public ConfiguratorInfo() {
        Configurator config = Configurator.getInstance();
        this._actionAcknowledgmentTimeout = config.getActionAcknowledgmentTimeout();
        this._keyInjectionDelay = config.getKeyInjectionDelay();
        this._scrollAcknowledgmentTimeout = config.getScrollAcknowledgmentTimeout();
        this._waitForIdleTimeout = config.getWaitForIdleTimeout();
        this._waitForSelectorTimeout = config.getWaitForSelectorTimeout();
    }

    public long getActionAcknowledgmentTimeout() {
        return _actionAcknowledgmentTimeout;
    }

    public void setActionAcknowledgmentTimeout(long _actionAcknowledgmentTimeout) {
        this._actionAcknowledgmentTimeout = _actionAcknowledgmentTimeout;
    }

    public long getKeyInjectionDelay() {
        return _keyInjectionDelay;
    }

    public void setKeyInjectionDelay(long _keyInjectionDelay) {
        this._keyInjectionDelay = _keyInjectionDelay;
    }

    public long getScrollAcknowledgmentTimeout() {
        return _scrollAcknowledgmentTimeout;
    }

    public void setScrollAcknowledgmentTimeout(long _scrollAcknowledgmentTimeout) {
        this._scrollAcknowledgmentTimeout = _scrollAcknowledgmentTimeout;
    }

    public long getWaitForIdleTimeout() {
        return _waitForIdleTimeout;
    }

    public void setWaitForIdleTimeout(long _waitForIdleTimeout) {
        this._waitForIdleTimeout = _waitForIdleTimeout;
    }

    public long getWaitForSelectorTimeout() {
        return _waitForSelectorTimeout;
    }

    public void setWaitForSelectorTimeout(long _waitForSelectorTimeout) {
        this._waitForSelectorTimeout = _waitForSelectorTimeout;
    }

    public static void setConfigurator(ConfiguratorInfo info) {
        Configurator config = Configurator.getInstance();
        config.setActionAcknowledgmentTimeout(info.getActionAcknowledgmentTimeout());
        config.setKeyInjectionDelay(info.getKeyInjectionDelay());
        config.setScrollAcknowledgmentTimeout(info.getScrollAcknowledgmentTimeout());
        config.setWaitForIdleTimeout(info.getWaitForIdleTimeout());
        config.setWaitForSelectorTimeout(info.getWaitForSelectorTimeout());
    }

    private long _actionAcknowledgmentTimeout;
    private long _keyInjectionDelay;
    private long _scrollAcknowledgmentTimeout;
    private long _waitForIdleTimeout;
    private long _waitForSelectorTimeout;
}
