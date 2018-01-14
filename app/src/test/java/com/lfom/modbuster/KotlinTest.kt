package com.lfom.modbuster

//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
import com.lfom.services.MqttClientAdapter
import com.lfom.signals.IntOptions
import com.lfom.signals.SignalChannel
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by gener on 14.01.2018.
 */

class KotlinTest {

    val mSignals: MutableMap<Int, SignalChannel> = ConcurrentHashMap()

    val mqttClients = arrayListOf<MqttClientAdapter>()

    private val serverUri = "tcp://192.168.10.11:1883"

    @Test
    fun fun1() {
        val clientId = "ExampleAndroidClient" + System.currentTimeMillis()
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.userName = "USER123"
        mqttConnectOptions.password = "ЛЕВblablaPassword".toCharArray()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.serverURIs = arrayOf("tcp://localhost:1883", "ssl://localhost:8883")
/*        val mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId)

        val newSignal = SignalChannel(33, IntOptions()).also {
            it.name = "WB_Rele_1"
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

        mqttClients.add(client)*/

        val moshi = Moshi.Builder()
                // Add any other JsonAdapter factories.
                .add(KotlinJsonAdapterFactory())
                .build()
        val adapter = moshi.adapter<IntOptions>(IntOptions::class.java)
        val str = adapter.toJson(IntOptions())
        println(str)

        val newSignal = SignalChannel(33, IntOptions()).also {
            it.name = "WB_Rele_1"
        }
        val strSignal = moshi.adapter<SignalChannel>(SignalChannel::class.java).toJson(newSignal)
        println(strSignal)


        /*       val gson = GsonBuilder().setPrettyPrinting().create()
        println(gson.toJson(mqttConnectOptions))
        println("=================")
        val newSignal = SignalChannel(33, IntOptions()).also {
            it.name = "WB_Rele_1"
        }
        println(gson.toJson(newSignal))

        val stringSignal =
                """
                    {
  "name": "WB_Rele_1",
  "refreshDataWhenPublish": false,
  "timePoint": 0,
    "arrivingDataEventManager": {
    "mListeners": []
  },
  "idx": 33,
  "options": {
    "trueString": "true",
    "falseString": "false",
    "highLevel": 1.0,
    "lowLevel": 0.0,
    "multiplier": 1.0,
    "shift": 0,
    "writeble": false
  }
}
                    """
        val newSignal1 = gson.fromJson(stringSignal, SignalChannel::class.java)*/
    }
}