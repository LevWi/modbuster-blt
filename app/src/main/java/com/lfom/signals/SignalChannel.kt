package com.lfom.signals


/**
 * Created by gener on 08.01.2018.
 */

data class SignalChannel(val idx: Int, val options: IPayloadCreator) : IPublishing, IArriving {

    @Transient
    var payload: SignalPayload? = null
        private set

    var name: String = ""

    var refreshDataWhenPublish = false

    @Transient
    var publishCallback: ((data: SignalPayload, sender: IPublishing?, receiver: SignalChannel?) -> Unit)? = null

    @Transient
    var arrivedCallback: ((data: SignalPayload, sender: IArriving?, receiver: SignalChannel?) -> Unit)? = null

    @Transient
    var publishListener: IPublishing? = null

    @Transient
    var timePoint : Long = 0

    val arrivingDataEventManager = ArrivingDataEventManager()

    fun notifyListeners(data: SignalPayload) {
        arrivingDataEventManager.notifyListeners(data, this)
    }

    override fun publish(data: SignalPayload, sender: IPublishing?) {
        if (refreshDataWhenPublish) {
            setInnerPayload(data, reverse = true)
        }
        var payloadBuf = options.create()
        when (data) {
            is IConvertible -> {
                if (!(payloadBuf as IConvertible).setFromPayload(data, reverse = true)){
                    payloadBuf = BadData(BadData.CONVERSION_ERROR)
                }
            }
            is BadData -> payloadBuf = data.copy()
        }
        publishCallback?.invoke(payloadBuf, sender, this)
        publishListener?.publish(payloadBuf, this)
    }

    override fun onNewPayload(data: SignalPayload, sender: IArriving?) {
        //runBlocking {
        //    mutex.withLock {
        setInnerPayload(data, reverse = false)
        //    }
        //}
        arrivedCallback?.invoke(data, sender, this)
        notifyListeners(payload ?: return)
    }

    fun setInnerPayload(data: SignalPayload, reverse : Boolean)  {
        timePoint = System.currentTimeMillis()
        when (data) {
            is IConvertible -> {
                if (payload == null || payload is BadData) {
                    payload = options.create()
                }
                (payload as IConvertible).setFromPayload(data, reverse)
            }
            is BadData -> payload = data.copy()
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