package com.lfom.services


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.lfom.signals.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Created by gener on 03.01.2018.
 */



class SignalsDataService : Service() {

    private val mSigDataServiceBinder = SigDataServiceBinder()

    /*TODO
    + проверка на уникальность Id сигнала

     TODO
     Если новое значение сигнала приходит от сервера
     - стандартное действие  , уведомляем подписанных приемников
     Пишем значение сами
     - другое действие, уведомляем подписанных приемников


     TODO Загрузка файла конфигурации

     */

    val mSignals: MutableMap<Int, SignalChannel> = ConcurrentHashMap()

    val mqttClients = arrayListOf<MqttClientAdapter>()

    inner class SigDataServiceBinder : Binder() {
        val service: SignalsDataService
            get() = this@SignalsDataService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mSigDataServiceBinder
    }

    private val serverUri = "tcp://192.168.10.11:1883"

    fun startWork() {

        val clientId = "ExampleAndroidClient" + System.currentTimeMillis()
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false

        val mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId)

        val newSignal = SignalChannel(33, IntOptions()).also {
            it.name = "WB_Rele_1"
            it.arrivedCallback = { data, _, _ ->
                val string = (data as? IConvertible)?.asString(null)
                Log.d(MAIN_DATA_SERVICE_TAG, "Received signal ${it.name} = $string")
            }
        }
        mSignals.put(newSignal.idx, newSignal)

        val client = MqttClientAdapter(mqttAndroidClient, mqttConnectOptions)
        client.addNewSignalEntry(
                client.MqttSignalEntry("/devices/wb-gpio/controls/Relay_1",
                        "/devices/wb-gpio/controls/Relay_1/on"
                ).also {
                    it.signal = newSignal
                    newSignal.publishListener = it
                }
        )

        mqttClients.add(client)
        client.connect()
    }
}

