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

const val TAG = "SignalsDataService"

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
                Log.d(TAG, "Received signal ${it.name} = $string")
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

class MqttClientAdapter(val mqttAndroidClient: MqttAndroidClient,
                        val mqttConnectOptions: MqttConnectOptions) {

    inner class MqttSignalEntry(val topicGeneral: String,
                                val topicForPublish: String = "",
                                val qos: Int = 0) : IPublishing {

        var signal: SignalChannel? = null

        val messageListener = IMqttMessageListener { topic, message ->
            signal?.let {
                val str = String(message.payload)
                Log.d(TAG, "Message arrived $topic = $str")
                it.onNewPayload(
                        StringPayload(StringOptions(), str),
                        null)
            }
        }

        override fun publish(data: SignalPayload, sender: IPublishing?) {
            if (data is IConvertible) {
                val payload = data.asString(null, reverse = true)
                val topic = if (topicForPublish != "") topicForPublish else topicGeneral
                publishMessageToService(topic, payload)
            }
        }
    }


    val mqttEntries = arrayListOf<MqttSignalEntry>()

    fun addNewSignalEntry(signal: MqttSignalEntry) {
        mqttEntries.add(signal)
    }

    fun connect() {

        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {

                if (reconnect) {
                    Log.i(TAG, "Reconnected to : " + serverURI)
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic()
                } else {
                    Log.i(TAG, "Connected to: " + serverURI)
                }
            }

            override fun connectionLost(cause: Throwable) {
                Log.w(TAG, "The Connection was lost.")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d(TAG, "Incoming message: $topic ${String(message.payload)}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    subscribeToTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.e(TAG, "Failed to connect to: " + mqttAndroidClient.serverURI)
                }
            })

        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        if (mqttEntries.isEmpty()) {
            Log.w(TAG, "Empty topics array for ${mqttAndroidClient.serverURI}")
            return
        }

        val topics = mqttEntries.map { it.topicGeneral }.toTypedArray()
        val qos = mqttEntries.map { it.qos }.toIntArray()
        val messageListeners = mqttEntries.map { it.messageListener }.toTypedArray()

        try {
            mqttAndroidClient.subscribe(topics, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(TAG, "Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(TAG, "Failed to subscribe")
                }
            })

            mqttAndroidClient.subscribe(topics, qos, messageListeners)

        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing")
            ex.printStackTrace()
        }

    }

    private fun publishMessageToService(topic: String, payloadString: String) {
        try {
            val message = MqttMessage()
            message.payload = payloadString.toByteArray()
            mqttAndroidClient.publish(topic, message)
            Log.v(TAG, "to service: $topic = $message")
            if (!mqttAndroidClient.isConnected) {
                Log.d(TAG, "${mqttAndroidClient.bufferedMessageCount} messages in buffer.")
            }
        } catch (e: MqttException) {
            Log.e(TAG, "Error Publishing: ${e.message}")
            e.printStackTrace()
        }

    }
}
