package com.github.uiautomator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class ToastActivity extends Activity {
    final static String TAG = "hiddenActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        Intent intent = getIntent();

        String message = intent.getStringExtra("message");
        if (message != null && !"".equals(message)) {
            Toast.makeText(this, "uiautomator say: " + message, Toast.LENGTH_LONG).show();
        }

        moveTaskToBack(true);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
