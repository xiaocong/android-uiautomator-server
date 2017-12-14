package com.github.uiautomator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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

            // restart service
            Intent intent = new Intent(MainActivity.this, Service.class);
            startService(intent);
            bindService(intent, connection, BIND_IMPORTANT | BIND_AUTO_CREATE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, Service.class);
        startService(intent);
        bindService(intent, connection, BIND_IMPORTANT | BIND_AUTO_CREATE);

        Button btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Service.class);
                stopService(intent);
                finish();
            }
        });

        Button btnIdentify = (Button) findViewById(R.id.btn_identify);
        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IdentifyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("theme", "RED");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.accessibility)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        ((Button) findViewById(R.id.development_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
//        stopService(new Intent(this, Service.class));
    }
}
