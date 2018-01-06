package com.lfom.signals

/**
 * Created by gener on 06.01.2018.
 */
enum class SignalType {
    BOOL,
    //BYTE,
    //SHORT,
    INT,
    FLOAT,
    //BYTE_ARR,
    STRING
}

enum class Quality(val code: Int) {
    BAD_ARGUMENT(3),
    ND(2), // нет данных
    GOOD(0),
    BAD(1)
}

sealed class SignalPayload

object Empty : SignalPayload()
data class BadData(val message: String = "BAD") : SignalPayload()
data class payloadString(var payload: String) : SignalPayload()
data class payloadInt(var payload: Int) : SignalPayload()
data class payloadFloat(var payload: Float) : SignalPayload()
data class payloadBool(var payload: Boolean) : SignalPayload()


class SignalChannel(val idx: Int, val type: SignalType, val name: String = "") : IPublishing, IArriving {
    var payload: SignalPayload = Empty
    var changeDataWhenPublish = false
    var publishListener: IPublishing? = null
    var publishCallback: ((data: SignalPayload?, signal: SignalChannel? ) -> Unit)? = null

    override fun publish(data: SignalPayload, signal: SignalChannel) {
        //publishCallback?.invoke(data, signal) TODO Нужен ли вообще ? Конвертер?
         if (changeDataWhenPublish) {
             TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
         }
         publishListener?.publish(data, this)
    }

    override fun onNewPayload(data: SignalPayload, signal: SignalChannel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * Для отправки значения на сервер
 */
interface IPublishing {
    fun publish(data: SignalPayload, signal : SignalChannel)
}

/**
 * Прием данных извне
 */
interface IArriving {
    fun onNewPayload(data: SignalPayload, signal : SignalChannel)
}