package com.github.uiautomator.stub;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by b036 on 12/26/13.
 */
public class ConfiguratorInfo {

    public ConfiguratorInfo() {
        try {
            Class clz = Class.forName("android.support.test.uiautomator.Configurator");
            Object conf = clz.getMethod("getInstance").invoke(null);
            this._actionAcknowledgmentTimeout = (Long)clz.getMethod("getActionAcknowledgmentTimeout").invoke(conf);
            this._keyInjectionDelay = (Long)clz.getMethod("getKeyInjectionDelay").invoke(conf);
            this._scrollAcknowledgmentTimeout = (Long)clz.getMethod("getScrollAcknowledgmentTimeout").invoke(conf);
            this._waitForIdleTimeout = (Long)clz.getMethod("getWaitForIdleTimeout").invoke(conf);
            this._waitForSelectorTimeout = (Long)clz.getMethod("getWaitForSelectorTimeout").invoke(conf);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        try {
            Class clz = Class.forName("android.support.test.uiautomator.Configurator");
            Object conf = clz.getMethod("getInstance").invoke(null);
            clz.getMethod("setActionAcknowledgmentTimeout", Long.TYPE).invoke(conf, info.getActionAcknowledgmentTimeout());
            clz.getMethod("setKeyInjectionDelay", Long.TYPE).invoke(conf, info.getKeyInjectionDelay());
            clz.getMethod("setScrollAcknowledgmentTimeout", Long.TYPE).invoke(conf, info.getScrollAcknowledgmentTimeout());
            clz.getMethod("setWaitForIdleTimeout", Long.TYPE).invoke(conf, info.getWaitForIdleTimeout());
            clz.getMethod("setWaitForSelectorTimeout", Long.TYPE).invoke(conf, info.getWaitForSelectorTimeout());
        } catch (IllegalAccessException e) {
            Log.d(e.getMessage());
        } catch (InvocationTargetException e) {
            Log.d(e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.d(e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d(e.getMessage());
        }
    }

    private long _actionAcknowledgmentTimeout;
    private long _keyInjectionDelay;
    private long _scrollAcknowledgmentTimeout;
    private long _waitForIdleTimeout;
    private long _waitForSelectorTimeout;
}
