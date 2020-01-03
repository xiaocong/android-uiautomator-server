package com.github.uiautomator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class ToastActivity extends Activity {
    final static String TAG = "ToastActivity";
    private static FloatView floatView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        Intent intent = getIntent();

        String message = intent.getStringExtra("message");
        if (message != null && !"".equals(message)) {
            Toast.makeText(this, "openatx: " + message, Toast.LENGTH_SHORT).show();
        }

        String showFloat = intent.getStringExtra("showFloatWindow");
        Log.i(TAG, "showFloat: " + showFloat);
        if ("true".equals(showFloat)) {
            getFloatView().show();
        } else if ("false".equals(showFloat)) {
            getFloatView().hide();
        }

        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private FloatView getFloatView() {
        if (floatView == null) {
            floatView = new FloatView(ToastActivity.this);
        }
        return floatView;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
