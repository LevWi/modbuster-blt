package com.lfom.signals

/**
 * Created by gener on 08.01.2018.
 */

class ArrivingDataEventManager {

    private val mListeners = arrayListOf<IArriving>()

    fun subscribe(listener: IArriving) {
        mListeners.firstOrNull({ it == listener }) ?: mListeners.add(listener)
    }

    fun unsubscribe(listener: IArriving) {
        mListeners.remove(listener)
    }

    fun notifyListeners(data: SignalPayload, sender: IArriving?) {
        mListeners.forEach { it.onNewPayload(data, sender) }
    }
}