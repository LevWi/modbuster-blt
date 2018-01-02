package com.lfom.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
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

    private final String TAG =  this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.v(TAG, "Verbose test message");
        Log.d(TAG, "Debug test message");
        Log.i(TAG, "Info test message");
        Log.e(TAG, "Error test message");
        Log.w(TAG, "Waring test message");
        Log.wtf(TAG, "WTF test message");
        Log.i(TAG, String.format("Debug level is loggable: %s", Log.isLoggable(TAG, Log.DEBUG)));
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

    public void onFindFile(View view) {
        Intent intent = new Intent(this, ConfigFileFinder.class);
        startActivity(intent);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    //barcodeValue.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    ConstraintLayout view = (ConstraintLayout)findViewById(R.id.consaraintLayout1);
                    Snackbar.make( view, "Barcode read: " + barcode.displayValue,
                            Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } /*else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }*/
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
