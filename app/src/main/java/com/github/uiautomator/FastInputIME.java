package com.github.uiautomator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class FastInputIME extends InputMethodService {
    private static final String TAG = "FastInputIME";
    private static final String USB_STATE_CHANGE = "android.hardware.usb.action.USB_STATE";

    private BroadcastReceiver mReceiver = null;

    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(USB_STATE_CHANGE);
        filter.addAction("ADB_INPUT_TEXT");
        filter.addAction("ADB_INPUT_CHARS");
        filter.addAction("ADB_EDITOR_CODE");
        mReceiver = new InputMessageReceiver();
        registerReceiver(mReceiver, filter);

        Keyboard keyboard = new Keyboard(this, R.xml.number_pad);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(new MyKeyboardActionListener());

        return keyboardView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    class InputMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case USB_STATE_CHANGE:
                    if (!intent.getExtras().getBoolean("connected")) {
                        final IBinder token = getWindow().getWindow().getAttributes().token;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.switchToLastInputMethod(token);
                    }
                    break;
                case "ADB_INPUT_TEXT":
                    String msg = intent.getStringExtra("text");
                    if (msg == null) {
                        return;
                    }
                    Log.i(TAG, "input text(base64): " + msg);
                    byte[] data = Base64.decode(msg, Base64.DEFAULT);
                    try {
                        String text = new String(data, "UTF-8");
                        InputConnection ic = getCurrentInputConnection();
                        ic.commitText(text, 1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case "ADB_CLEAR_TEXT":
                    // TODO: not finished yet
                    break;
            }
        }
    }

    // Refs: https://www.jianshu.com/p/892168a57fe3
    private class MyKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {

        @Override
        public void onPress(int i) {
        }

        @Override
        public void onRelease(int i) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                Log.d(TAG, "Keyboard CANCEL not implemented");
            } else if (primaryCode == -10) {
                clearText();
            } else if (primaryCode == -5) {
                switchToLastInputMethod();
            } else if (primaryCode == -7) {
                InputConnection ic = getCurrentInputConnection();
                ic.commitText(randomString(1), 0);
            } else {
                Log.w(TAG, "Unknown primaryCode " + primaryCode);
            }
        }

        @Override

        public void onText(CharSequence charSequence) {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeUp() {
        }
    }

    private void clearText() {
        // Refs: https://stackoverflow.com/questions/33082004/android-custom-soft-keyboard-how-to-clear-edit-text-commited-text
        InputConnection ic = getCurrentInputConnection();
        CharSequence currentText = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
        Log.d(TAG, "Current text: " + currentText);
        CharSequence beforCursorText = ic.getTextBeforeCursor(currentText.length(), 0);
        CharSequence afterCursorText = ic.getTextAfterCursor(currentText.length(), 0);
        ic.deleteSurroundingText(beforCursorText.length(), afterCursorText.length());
    }

    private void switchToLastInputMethod() {
        final IBinder token = getWindow().getWindow().getAttributes().token;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.switchToLastInputMethod(token);
    }

    public String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }
}
