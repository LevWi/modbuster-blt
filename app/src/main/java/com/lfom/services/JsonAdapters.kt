package com.lfom.services

import android.content.Context
import com.lfom.signals.SignalChannel
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.ToJson
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import kotlin.collections.ArrayList





class MqttClientHelperJson(
        @Json(name = "uri") val serverUri : String ,
        @Json(name = "connect_id") val clientId : String? = null,
        @Json(name = "options") val mqttConnectOptions: MqttConnectOptions,
        @Json(name = "topics") val mqttSignalEntryList: ArrayList<MqttClientHelper.MqttSignalEntry> = arrayListOf()
)


class MqttClientHelperJsonAdapter(val context: Context) {
    @FromJson
    fun clientFromJson(json : MqttClientHelperJson) : MqttClientHelper {
        val mqttAndroidClient = MqttAndroidClient(
                context,
                json.serverUri,
                "${json.clientId}_${System.currentTimeMillis()}"
        )
        return with(MqttClientHelper(mqttAndroidClient, json.mqttConnectOptions))
        {
            json.mqttSignalEntryList.forEach {
                addNewSignalEntry(it)
            }
            this
        }
    }

    @ToJson
    fun clientToJson(clientHelper : MqttClientHelper) : MqttClientHelperJson {
        return MqttClientHelperJson(
                clientHelper.mqttAndroidClient.serverURI,
                clientHelper.mqttAndroidClient.clientId,
                clientHelper.mqttConnectOptions,
                clientHelper.mqttEntries
        )
    }
}

class MqttConnectOptionsJson(
        @Json(name = "keep_alive_interval") val keepAliveInterval: Int = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT,
        @Json(name = "user_name") val userName: String? = null,
        val password: String? = null,
        @Json(name = "clean_session") val cleanSession: Boolean = false,
        @Json(name = "connection_timeout") val connectionTimeout: Int = MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT,
        @Json(name = "automatic_reconnect") val automaticReconnect: Boolean = true
        //val maxInflight : Int = MqttConnectOptions.MAX_INFLIGHT_DEFAULT,
        //val willDestination: String? = null,
        //val willMessage: MqttMessage? = null,
        //val socketFactory: SocketFactory? = null,
        //val sslClientProps: Properties? = null,
        //val serverURIs: Array<String>? = null,
        //val MqttVersion = MqttConnectOptions.MQTT_VERSION_DEFAULT,
)

class MqttConnectOptionsJsonAdapter {
    @FromJson
    fun optionsFromJson(mqttConnectOptionsJson: MqttConnectOptionsJson): MqttConnectOptions {
        return with(MqttConnectOptions()) {
            keepAliveInterval = mqttConnectOptionsJson.keepAliveInterval
            userName = mqttConnectOptionsJson.userName
            password = mqttConnectOptionsJson.password?.toCharArray()
            isCleanSession = mqttConnectOptionsJson.cleanSession
            connectionTimeout = mqttConnectOptionsJson.connectionTimeout
            isAutomaticReconnect = mqttConnectOptionsJson.automaticReconnect
            this
        }
    }

    @ToJson
    fun optionsToJson(mqttConnectOptions: MqttConnectOptions): MqttConnectOptionsJson {
        return MqttConnectOptionsJson(
                keepAliveInterval = mqttConnectOptions.keepAliveInterval,
                userName = mqttConnectOptions.userName,
                password = mqttConnectOptions.password?.toString(),
                cleanSession = mqttConnectOptions.isCleanSession,
                connectionTimeout = mqttConnectOptions.connectionTimeout,
                automaticReconnect = mqttConnectOptions.isAutomaticReconnect
        )

    }
}