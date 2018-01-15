package com.lfom.services

import com.lfom.signals.SignalChannel
import com.squareup.moshi.Json


class ServiceConfig(
        val signals: MutableMap<Int, SignalChannel> = mutableMapOf(),
        @Json(name = "mqtt_connections") val mqttClients: ArrayList<MqttClientHelper> = arrayListOf()
)