package com.lfom.services

import com.lfom.signals.SignalChannel
import com.squareup.moshi.Json


class ServiceConfigJson(
        val groups: MutableList<GroupSignals> = arrayListOf(),
        val signals: MutableMap<Int, SignalChannel> = mutableMapOf(),
        @Json(name = "mqtt") val mqttClients: List<MqttClientHelperJson> = arrayListOf()
)

data class GroupSignals(var id: Int = 0) {
    var name: String = ""
    var signals = mutableSetOf<Int>() // Индексы сигналов для визуального отображения
}