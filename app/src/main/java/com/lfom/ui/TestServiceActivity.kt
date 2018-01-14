package com.lfom.ui

import kotlinx.android.synthetic.main.activity_test_service.*

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.lfom.modbuster.R

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import com.lfom.services.SignalsDataService
import com.lfom.signals.*

class TestServiceActivity : AppCompatActivity() {

    var boundService: SignalsDataService? = null

    val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            boundService = (service as? SignalsDataService.SigDataServiceBinder)?.service
            boundService?.startWork()
            val signal = boundService?.mSignals?.get(33)
            signal?.arrivingDataEventManager!!.subscribe(object : IArriving {
                override fun onNewPayload(data: SignalPayload, sender: IArriving?) {
                    runOnUiThread {
                        if (data is IConvertible)
                            textView2.text = data.asString(null)
                    }
                }
            }
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_service)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SignalsDataService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun onReleOn(view: View) {
        boundService?.mSignals?.get(33)?.publish(BoolPayload(BoolOptions(), true), null)

    }

    fun onReleOff(view: View) {
        boundService?.mSignals?.get(33)?.publish(BoolPayload(BoolOptions(), false), null)
    }
}