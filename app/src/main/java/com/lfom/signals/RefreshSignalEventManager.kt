package com.lfom.signals

/**
 * Created by gener on 05.11.2017.
 */


class RefreshSignalEventManager {

    private val listeners = arrayListOf<RefreshSignalListener>()

    fun subscribe(listener: RefreshSignalListener) {
        listeners.firstOrNull({ it == listener }) ?: listeners.add(listener)
    }

    fun unsubscribe(listener: RefreshSignalListener) {
        listeners.remove(listener)
    }

    fun notifyListners(obj: IDataOut) {
        listeners.forEach { it.update(obj) }
    }
}

interface RefreshSignalListener {
    fun update(obj: IDataOut)
}