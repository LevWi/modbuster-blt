package com.lfom.signals

/**
 * Created by gener on 08.01.2018.
 */

class ArrivingDataEventManager {

    val listeners = arrayListOf<IArriving>()

    fun subscribe(listener: IArriving) {
        listeners.firstOrNull({ it == listener }) ?: listeners.add(listener)
    }

    fun unsubscribe(listener: IArriving) {
        listeners.remove(listener)
    }

    fun notifyListeners(data: SignalPayload, sender: IArriving?) {
        listeners.forEach { it.onNewPayload(data, sender) }
    }
}