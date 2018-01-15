package com.lfom.services

import android.util.Log
import com.lfom.signals.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

/**
 *
 */
class MqttClientHelper(val mqttAndroidClient: MqttAndroidClient,
                       val mqttConnectOptions: MqttConnectOptions) {

    class MqttSignalEntry(val topicGeneral: String,
                          val topicForPublish: String? = "",
                          val idxReceiver: Int = 0
    ) : IPublishing {

        var qos: Int = 0

        @Transient
        var client : MqttClientHelper? = null

        @Transient
        var receiver: SignalChannel? = null

        @Transient
        val messageListener = IMqttMessageListener { topic, message ->
            receiver?.let {
                val str = String(message.payload)
                Log.d(MAIN_DATA_SERVICE_TAG, "Message arrived $topic = $str")
                it.onNewPayload(
                        StringPayload(StringOptions(), str),
                        null)
            }
        }

        override fun publish(data: SignalPayload, sender: IPublishing?) {
            client ?: return
            if (data is IConvertible) {
                val payload = data.asString(null, reverse = true)
                val topic = if (topicForPublish == "" || topicForPublish == null) topicGeneral else topicForPublish
                client?.publishMessageToService(topic, payload)
            }
        }
    }

    val mqttEntries = arrayListOf<MqttSignalEntry>()

    fun addNewSignalEntry(signal: MqttSignalEntry) {
        mqttEntries.add(signal)
        signal.client = this
    }

    fun connect() {

        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {

                if (reconnect) {
                    Log.i(MAIN_DATA_SERVICE_TAG, "Reconnected to : " + serverURI)
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic()
                } else {
                    Log.i(MAIN_DATA_SERVICE_TAG, "Connected to: " + serverURI)
                }
            }

            override fun connectionLost(cause: Throwable) {
                Log.w(MAIN_DATA_SERVICE_TAG, "The Connection was lost.")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d(MAIN_DATA_SERVICE_TAG, "Incoming message: $topic ${String(message.payload)}")
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
                    Log.e(MAIN_DATA_SERVICE_TAG, "Failed to connect to: " + mqttAndroidClient.serverURI)
                }
            })

        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        if (mqttEntries.isEmpty()) {
            Log.w(MAIN_DATA_SERVICE_TAG, "Empty topics array for ${mqttAndroidClient.serverURI}")
            return
        }

        val topics = mqttEntries.map { it.topicGeneral }.toTypedArray()
        val qos = mqttEntries.map { it.qos }.toIntArray()
        val messageListeners = mqttEntries.map { it.messageListener }.toTypedArray()

        try {
            mqttAndroidClient.subscribe(topics, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(MAIN_DATA_SERVICE_TAG, "Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(MAIN_DATA_SERVICE_TAG, "Failed to subscribe")
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
            Log.v(MAIN_DATA_SERVICE_TAG, "to service: $topic = $message")
            if (!mqttAndroidClient.isConnected) {
                Log.d(MAIN_DATA_SERVICE_TAG, "${mqttAndroidClient.bufferedMessageCount} messages in buffer.")
            }
        } catch (e: MqttException) {
            Log.e(MAIN_DATA_SERVICE_TAG, "Error Publishing: ${e.message}")
            e.printStackTrace()
        }

    }
}