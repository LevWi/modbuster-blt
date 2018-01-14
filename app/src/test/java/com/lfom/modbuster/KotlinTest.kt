package com.lfom.modbuster

import com.lfom.services.MqttClientAdapter
import com.lfom.signals.SignalChannel
//import com.squareup.moshi.KotlinJsonAdapterFactory
//import com.squareup.moshi.Moshi
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
    fun fun1(){
        val clientId = "ExampleAndroidClient" + System.currentTimeMillis()
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false

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

/*        val moshi = Moshi.Builder()
                // Add any other JsonAdapter factories.
                .add(KotlinJsonAdapterFactory())
                .build()
        val adapter = moshi.adapter<MqttConnectOptions>(MqttConnectOptions::class.java)
        val str = adapter.toJson(mqttConnectOptions)*/
        println()
    }
}