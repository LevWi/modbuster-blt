package com.lfom.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lfom.modbuster.R;
import com.lfom.ui.barcode.BarcodeCaptureActivity;




public class MainActivity extends AppCompatActivity {

    /*
    BluetoothSPPService mService;
    // Обратный вызов Сервиса
    ISigServCallBack mISigServCallBack = new ISigServCallBack() {
        @Override
        public void OnNewData( final int i) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.textView)).setText(String.valueOf(i));
                        }
                    });
        }
    };

    boolean mBound = false;

    public BluetoothAdapter mBluetoothAdapter;
    public final int REQUEST_ENABLE_BT = 10;

    */

    private final String LOG_TAG =  this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        //Intent intent = new Intent(this, BluetoothSPPService.class);

        //boolean result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //Log.w(LOG_TAG, "bindService" + (result ? " true" : "false") );
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    /*
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
    }; */

    private static final int RC_BARCODE_CAPTURE = 9001;

    public void onStartScanBarcode(View view) {
        // launch barcode activity.
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }
}
