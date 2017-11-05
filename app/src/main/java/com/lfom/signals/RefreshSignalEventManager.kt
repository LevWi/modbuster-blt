package com.lfom.signals

/**
 * Created by gener on 05.11.2017.
 */

/*TODO Унаследовать его сигналом.
* Сигнал должен реализовать
 * методы приведения
 * соответствующий метод будет вызываться соответствующим классом*/
abstract class RefreshSignalEventManager {

    var listeners: Map<String, List<EventListener>> = HashMap()

    fun subscribe(String eventType, EventListener listener) {
    }

    fun   unsubscribe(String eventType, EventListener listener) {
    }

    fun   notify

}