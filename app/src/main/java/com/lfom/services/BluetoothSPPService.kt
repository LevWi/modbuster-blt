package com.lfom.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log


/**
 * Created by gener on 01.11.2017.
 */

class BluetoothSPPService : Service() {
    // Binder given to clients
    private val mBinder = LocalBinder()

    private val LOG_TAG = this.javaClass.simpleName

    var mISigServCallBack: ISigServCallBack? = null


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        val service: BluetoothSPPService
            get() = this@BluetoothSPPService
    }


    private fun TestTask() {
        Thread(Runnable {
            for (i in 1..30) {
                /*TODO Не работает Log debug*/ Log.d(LOG_TAG, "i = " + i)
                try {
                    Thread.sleep(1000)
                    if (mISigServCallBack != null) {
                        mISigServCallBack!!.OnNewData(i)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            stopSelf()
        }).start()
    }

    fun setCallback(iSigServCallBack: ISigServCallBack) {
        mISigServCallBack = iSigServCallBack
        TestTask()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

}

