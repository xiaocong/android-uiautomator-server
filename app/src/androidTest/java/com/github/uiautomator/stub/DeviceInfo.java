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

import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

public class DeviceInfo {
	private String _currentPackageName;
	private int _displayWidth;
	private int _displayHeight;
	private int _displayRotation;
	private int _displaySizeDpX;
	private int _displaySizeDpY;
	private String _productName;
	private boolean _naturalOrientation;
    private boolean _screenOn;
	
	private int _sdkInt;

    public static DeviceInfo getDeviceInfo() {
        return new DeviceInfo();
    }

	private DeviceInfo() {
		this._sdkInt = android.os.Build.VERSION.SDK_INT;

		UiDevice ud = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		this._currentPackageName = ud.getCurrentPackageName();
		this._displayWidth = ud.getDisplayWidth();
		this._displayHeight = ud.getDisplayHeight();
		this._displayRotation = ud.getDisplayRotation();
		this._productName = ud.getProductName();
		this._naturalOrientation = ud.isNaturalOrientation();
		this._displaySizeDpX = ud.getDisplaySizeDp().x;
		this._displaySizeDpY = ud.getDisplaySizeDp().y;
        try {
            this._screenOn = ud.isScreenOn();
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(e.getMessage());
        }
    }
	
	public String getCurrentPackageName() {
		return _currentPackageName;
	}

	public void setCurrentPackageName(String currentPackageName) {
		this._currentPackageName = currentPackageName;
	}

	public int getDisplayWidth() {
		return _displayWidth;
	}

	public void setDisplayWidth(int displayWidth) {
		this._displayWidth = displayWidth;
	}

	public int getDisplayHeight() {
		return _displayHeight;
	}

	public void setDisplayHeight(int displayHeight) {
		this._displayHeight = displayHeight;
	}

	public int getDisplayRotation() {
		return _displayRotation;
	}

	public void setDisplayRotation(int displayRotation) {
		this._displayRotation = displayRotation;
	}

	public int getDisplaySizeDpX() {
		return _displaySizeDpX;
	}

	public void setDisplaySizeDpX(int displaySizeDpX) {
		this._displaySizeDpX = displaySizeDpX;
	}

	public int getDisplaySizeDpY() {
		return _displaySizeDpY;
	}

	public void setDisplaySizeDpY(int displaySizeDpY) {
		this._displaySizeDpY = displaySizeDpY;
	}

	public String getProductName() {
		return _productName;
	}

	public void setProductName(String productName) {
		this._productName = productName;
	}

	public boolean isNaturalOrientation() {
		return _naturalOrientation;
	}

	public void setNaturalOrientation(boolean naturalOrientation) {
		this._naturalOrientation = naturalOrientation;
	}

	public int getSdkInt() {
		return _sdkInt;
	}

	public void setSdkInt(int sdkInt) {
		this._sdkInt = sdkInt;
	}

    public boolean getScreenOn() {
        return _screenOn;
    }

    public void setScreenOn(boolean screenOn) {
        this._screenOn = screenOn;
    }
}
