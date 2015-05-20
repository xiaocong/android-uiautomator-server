package com.github.uiautomator.stub;

public class Log {
	public static final String TAG = "UIAutomatorStub";
	
	public static void d(String msg) {
		android.util.Log.d(TAG, msg);
	}

	public static void i(String msg) {
		android.util.Log.i(TAG, msg);
	}

	public static void e(String msg) {
		android.util.Log.e(TAG, msg);
	}
}
