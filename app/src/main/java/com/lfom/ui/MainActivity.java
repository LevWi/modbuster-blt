package com.lfom.ui;


import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.lfom.modbuster.R;
import com.lfom.services.BluetoothSPPService;
import com.lfom.services.ISigServCallBack;




public class MainActivity extends AppCompatActivity {

    BluetoothSPPService mService;



    // Обратный вызов Сервиса
    ISigServCallBack mISigServCallBack = new ISigServCallBack() {
        @Override
        public void OnNewData(/*TODO ???? Почему требуется final*/ final int i) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.textView)).setText(String.valueOf(i));
                        }
                    });
        }
    };

    private final String LOG_TAG =  this.getClass().getSimpleName();

    boolean mBound = false;

    public BluetoothAdapter mBluetoothAdapter;
    public final int REQUEST_ENABLE_BT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, BluetoothSPPService.class);

        boolean result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.w(LOG_TAG, "bindService" + (result ? " true" : "false") );
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void startCommand(View view) {
        if (mBound) {
            mService.setCallback(mISigServCallBack);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BluetoothSPPService.LocalBinder binder = (BluetoothSPPService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
