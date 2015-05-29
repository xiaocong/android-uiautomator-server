package com.github.uiautomator.stub;

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
	
	private int _sdkInt;

    public final static DeviceInfo getDeviceInfo() {
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
}
