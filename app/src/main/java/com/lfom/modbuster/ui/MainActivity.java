//package com.lfom.modbuster.ui;
//
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.constraint.ConstraintLayout;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.google.android.gms.common.api.CommonStatusCodes;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.lfom.modbuster.R;
//import com.lfom.modbuster.ui.barcode.BarcodeCaptureActivity;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//
//public class MainActivity extends AppCompatActivity {
//
//    /*
//    BluetoothSPPService mService;
//    // Обратный вызов Сервиса
//    ISigServCallBack mISigServCallBack = new ISigServCallBack() {
//        @Override
//        public void OnNewData( final int i) {
//            runOnUiThread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            ((TextView) findViewById(R.id.textView)).setText(String.valueOf(i));
//                        }
//                    });
//        }
//    };
//
//    boolean mBound = false;
//
//    public BluetoothAdapter mBluetoothAdapter;
//    public final int REQUEST_ENABLE_BT = 10;
//
//    */
//
//    private final String TAG = this.getClass().getSimpleName();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    MqttAndroidClient mqttAndroidClient1;
//    MqttAndroidClient mqttAndroidClient2;
//    String clientId = "ExampleAndroidClient";
//    final String serverUri = "tcp://iot.eclipse.org:1883";
//    final String serverUri2 = "tcp://192.168.10.11:1883";
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.v(TAG, "Verbose test message");
//        Log.d(TAG, "Debug test message");
//        Log.i(TAG, "Info test message");
//        Log.e(TAG, "Error test message");
//        Log.w(TAG, "Waring test message");
//        Log.wtf(TAG, "WTF test message");
//        Log.i(TAG, String.format("Debug level is loggable: %s", Log.isLoggable(TAG, Log.DEBUG)));
//        // Bind to LocalService
//        //Intent intent = new Intent(this, BluetoothSPPService.class);
//
//        //boolean result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        //Log.w(LOG_TAG, "bindService" + (result ? " true" : "false") );
//
//        clientId = clientId + System.currentTimeMillis();
//
//        mqttAndroidClient2 = new MqttAndroidClient(getApplicationContext(), serverUri2, clientId + "2");
//        mqttAndroidClient2.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//                if (reconnect) {
//                    Log.i(TAG, "Reconnected to : " + serverURI);
//                    // Because Clean Session is true, we need to re-subscribe
//                    subscribeToTopic2();
//                } else {
//                    Log.i(TAG, "Connected to: " + serverURI);
//                }
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Log.w(TAG, "The Connection was lost.");
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                Log.i(TAG, "Incoming message: " + new String(message.getPayload()));
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//
//        mqttAndroidClient1 = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
//        mqttAndroidClient1.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//                if (reconnect) {
//                    Log.i(TAG, "Reconnected to : " + serverURI);
//                    // Because Clean Session is true, we need to re-subscribe
//                    subscribeToTopic1();
//                } else {
//                    Log.i(TAG, "Connected to: " + serverURI);
//                }
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Log.w(TAG, "The Connection was lost.");
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                Log.i(TAG, "Incoming message: " + new String(message.getPayload()));
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//
//
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setAutomaticReconnect(true);
//        mqttConnectOptions.setCleanSession(false);
//
//        try {
//            //addToHistory("Connecting to " + serverUri);
//            mqttAndroidClient1.connect(mqttConnectOptions, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
//                    disconnectedBufferOptions.setBufferEnabled(true);
//                    disconnectedBufferOptions.setBufferSize(100);
//                    disconnectedBufferOptions.setPersistBuffer(false);
//                    disconnectedBufferOptions.setDeleteOldestMessages(false);
//                    mqttAndroidClient1.setBufferOpts(disconnectedBufferOptions);
//                    subscribeToTopic1();
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.e(TAG, "Failed to connect to: " + mqttAndroidClient1.getServerURI());
//                }
//            });
//
//        } catch (MqttException ex) {
//            ex.printStackTrace();
//        }
//        try {
//            //addToHistory("Connecting to " + serverUri);
//            mqttAndroidClient2.connect(mqttConnectOptions, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
//                    disconnectedBufferOptions.setBufferEnabled(true);
//                    disconnectedBufferOptions.setBufferSize(100);
//                    disconnectedBufferOptions.setPersistBuffer(false);
//                    disconnectedBufferOptions.setDeleteOldestMessages(false);
//                    mqttAndroidClient2.setBufferOpts(disconnectedBufferOptions);
//                    subscribeToTopic2();
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.e(TAG, "Failed to connect to: " + mqttAndroidClient2.getServerURI());
//                }
//            });
//
//
//        } catch (MqttException ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//    }
//
//
//    public void subscribeToTopic1() {
//        String subscriptionTopic = "/uwblogs";
//        try {
//            mqttAndroidClient1.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.i(TAG, "Subscribed!");
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.i(TAG, "Failed to subscribe");
//                }
//            });
//
//            // THIS DOES NOT WORK!
//            mqttAndroidClient1.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // message Arrived!
//                    final String str = new String(message.getPayload());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((TextView) findViewById(R.id.textView_server1)).setText(str);
//                        }
//                    });
//
//                    Log.i(TAG, "Message: " + topic + " : " + new String(message.getPayload()));
//                }
//            });
//
//        } catch (MqttException ex) {
//            System.err.println("Exception whilst subscribing");
//            ex.printStackTrace();
//        }
//    }
//
//    public void subscribeToTopic2() {
//        String subscriptionTopic = "/devices/wb-adc/controls/Vin";
//        try {
//            mqttAndroidClient2.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.i(TAG, "Subscribed!");
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.i(TAG, "Failed to subscribe");
//                }
//            });
//
//            mqttAndroidClient2.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // message Arrived!
//                    final String str = new String(message.getPayload());
//                    final TextView view = findViewById(R.id.textView_server2);
//                    view.post(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    view.setText(str);
//                                }
//                            });
//                    Log.i(TAG, "Message server2: " + topic + " : " + new String(message.getPayload()));
//                }
//            });
//
//        } catch (MqttException ex) {
//            System.err.println("Exception whilst subscribing");
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * Defines callbacks for service binding, passed to bindService()
//     */
//    /*
//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            BluetoothSPPService.LocalBinder binder = (BluetoothSPPService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    }; */
//
//    private static final int RC_BARCODE_CAPTURE = 9001;
//
//    public void onStartScanBarcode(View view) {
//        // launch barcode activity.
//        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
//        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
//        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
//
//        startActivityForResult(intent, RC_BARCODE_CAPTURE);
//    }
//
//    public void onFindFile(View view) {
//        Intent intent = new Intent(this, ConfigFileFinder.class);
//        startActivity(intent);
//    }
//
//    /**
//     * Called when an activity you launched exits, giving you the requestCode
//     * you started it with, the resultCode it returned, and any additional
//     * data from it.  The <var>resultCode</var> will be
//     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
//     * didn't return any result, or crashed during its operation.
//     * <p/>
//     * <p>You will receive this call immediately before onResume() when your
//     * activity is re-starting.
//     * <p/>
//     *
//     * @param requestCode The integer request code originally supplied to
//     *                    startActivityForResult(), allowing you to identify who this
//     *                    result came from.
//     * @param resultCode  The integer result code returned by the child activity
//     *                    through its setResult().
//     * @param data        An Intent, which can return result data to the caller
//     *                    (various data can be attached to Intent "extras").
//     * @see #startActivityForResult
//     * @see #createPendingResult
//     * @see #setResult(int)
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RC_BARCODE_CAPTURE) {
//            if (resultCode == CommonStatusCodes.SUCCESS) {
//                if (data != null) {
//                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
//                    //statusMessage.setText(R.string.barcode_success);
//                    //barcodeValue.setText(barcode.displayValue);
//                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
//                    ConstraintLayout view = findViewById(R.id.consaraintLayout1);
//                    Snackbar.make(view, "Barcode read: " + barcode.displayValue,
//                            Snackbar.LENGTH_LONG)
//                            .show();
//                } else {
//                    //statusMessage.setText(R.string.barcode_failure);
//                    Log.d(TAG, "No barcode captured, intent data is null");
//                }
//            } /*else {
//                statusMessage.setText(String.format(getString(R.string.barcode_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
//            }*/
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//}
