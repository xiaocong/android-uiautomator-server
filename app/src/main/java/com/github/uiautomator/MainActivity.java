package com.github.uiautomator;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
    private final String TAG = "ATXMainActivity";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "service disconnected");
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ensureVisibility();
        Intent intent = new Intent(this, Service.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        Button btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ensureVisibility();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void ensureVisibility() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        unlock();

        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = 1.0f;
        window.setAttributes(params);
    }

    @SuppressWarnings("deprecation")
    private void unlock() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        keyguardManager.newKeyguardLock("InputService/Unlock").disableKeyguard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        stopService(new Intent(this, Service.class));
    }
}
