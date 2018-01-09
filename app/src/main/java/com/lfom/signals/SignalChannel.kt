package com.lfom.signals

import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

/**
 * Created by gener on 08.01.2018.
 */
class SignalChannel(val idx: Int, val options: ConvertOptions) : IPublishing, IArriving {
    var payload: SignalPayload? = null
        private set
    var name: String = ""
    var refreshDataWhenPublish = false

    var publishCallback: ((data: SignalPayload, sender: IPublishing?) -> Unit)? = null
    var arrivedCallback: ((data: SignalPayload, sender: IArriving?) -> Unit)? = null

    var publishListener: IPublishing? = null

    private val arrivingDataEventManager = ArrivingDataEventManager()
    private val mutex = Mutex()

    fun notifyListeners(data: SignalPayload) {
        arrivingDataEventManager.notifyListeners(data, this)
    }

    override fun publish(data: SignalPayload, sender: IPublishing?) {
        if (refreshDataWhenPublish) {
            setInnerPayload(data)
        }
        publishCallback?.invoke(data, sender)
        TODO("Нет обратного преобразования")
        publishListener?.publish(data, this)
    }

    override fun onNewPayload(data: SignalPayload, sender: IArriving?) {
        runBlocking {
            mutex.withLock {
                setInnerPayload(data)
            }
        }
        arrivedCallback?.invoke(data, sender)
        notifyListeners(payload ?: return)
    }

    fun setInnerPayload(data: SignalPayload) {
            when (data) {
                is IConvertible -> {
                    if (payload == null || payload is SignalPayload.BadData) {
                        payload = SignalPayload.createInstance(options)
                    }
                    (payload as IConvertible).setFromPayload(data)
                }
                is SignalPayload.BadData -> this.payload = data.copy()
            }
        }
}



/**
 * Для отправки значения на сервер
 */
interface IPublishing {
    fun publish(data: SignalPayload, sender: IPublishing?)
}

/**
 * Прием данных извне
 */
interface IArriving {
    fun onNewPayload(data: SignalPayload, sender: IArriving?)
}