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
