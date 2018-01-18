package com.lfom.signals

import com.squareup.moshi.Json


class ArrivingDataEventManager {

    @Json(name = "ids") var listnersIds : MutableList<Int> = mutableListOf()

    @Transient
    private val mListeners = mutableSetOf<IArriving>()

    fun subscribe(listener: IArriving) {
        //mListeners.firstOrNull({ it === listener }) ?: mListeners.add(listener)
        mListeners.add(listener)
    }

    fun unsubscribe(listener: IArriving) {
        mListeners.remove(listener)
    }

    fun notifyListeners(data: SignalPayload, sender: IArriving?) {
        mListeners.forEach { it.onNewPayload(data, sender) }
    }
}