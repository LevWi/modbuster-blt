package com.lfom.signals



/**
 * Created by gener on 08.01.2018.
 */
class SignalChannel(val idx: Int, val options: IPayloadCreator) : IPublishing, IArriving {
    var payload: SignalPayload? = null
        private set
    var name: String = ""
    var refreshDataWhenPublish = false

    var publishCallback: ((data: SignalPayload, sender: IPublishing?, receiver: SignalChannel?) -> Unit)? = null
    var arrivedCallback: ((data: SignalPayload, sender: IArriving?, receiver: SignalChannel?) -> Unit)? = null

    var publishListener: IPublishing? = null
    var refreshTimePoint = Date(0) // TODO Формат хранения записи ??

    private val arrivingDataEventManager = ArrivingDataEventManager()

    fun notifyListeners(data: SignalPayload) {
        arrivingDataEventManager.notifyListeners(data, this)
    }

    override fun publish(data: SignalPayload, sender: IPublishing?) {
        /* TODO

            -- для обратного преобразования использовать настройки пришедшего data
            т.к. текущий канал не знает какие преобразование претерпели данные
            -- без refreshDataWhenPublish не обновлять данные , а только преобразовывать на основе их копии
         */
        var payloadBuf = options.create()
        if (refreshDataWhenPublish) {
            InverseSetInnerPayload(data)  // TODO("Нужно обратное преобразование")
            payload?.let { payloadBuf = it }

        }
        publishCallback?.invoke(data, sender, this)
        publishListener?.publish(data, this)
    }

    override fun onNewPayload(data: SignalPayload, sender: IArriving?) {
        //runBlocking {
        //    mutex.withLock {
        setInnerPayload(data)
        //    }
        //}
        arrivedCallback?.invoke(data, sender, this)
        notifyListeners(payload ?: return)
    }

    fun setInnerPayload(data: SignalPayload) {
        when (data) {
            is IConvertible -> {
                if (payload == null || payload is BadData) {
                    payload = options.create()
                }
                (payload as IConvertible).setFromPayload(data) // TODO Отработка ошибки конвертации
            }
            is BadData -> this.payload = data.copy()
        }
        refreshTimePoint = Date()
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