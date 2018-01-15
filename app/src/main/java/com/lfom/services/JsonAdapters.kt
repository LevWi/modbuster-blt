package com.lfom.services

import com.lfom.signals.SignalChannel
import com.squareup.moshi.FromJson
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*
import javax.net.SocketFactory


class JsonServiceConfig {
    val signals = mutableMapOf<Int, SignalChannel>()
    val mqttClients = arrayListOf<MqttClientHelper>()
}

class MattClientHelperJson {
    val mqttAndroidClient: MqttAndroidClient
    val mqttConnectOptions: MqttConnectOptions
}

class MattClientHelperJsonAdapter {

}



class MqttConnectOptionsJson(
        val keepAliveInterval: Int = MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT,
        //val maxInflight : Int = MqttConnectOptions.MAX_INFLIGHT_DEFAULT,
        //val willDestination: String? = null,
        //val willMessage: MqttMessage? = null,
        val userName: String? = null,
        val password: CharArray? = null,
        //val socketFactory: SocketFactory? = null,
        //val sslClientProps: Properties? = null,
        val cleanSession: Boolean = MqttConnectOptions.CLEAN_SESSION_DEFAULT,
        val connectionTimeout: Int = MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT,
        //val serverURIs: Array<String>? = null,
        //val MqttVersion = MqttConnectOptions.MQTT_VERSION_DEFAULT,
        val automaticReconnect: Boolean = true
)

class MqttConnectOptionsJsonAdapter{
    @FromJson fun  elementFromJson (mqttConnectOptionsJson : MqttConnectOptionsJson){

    }
}