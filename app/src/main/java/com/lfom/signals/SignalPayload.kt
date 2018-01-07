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
data class BadData(val message: String = "BAD") : SignalPayload() {
    override fun toString(): String {
        return message
    }
}

data class payloadString(private var payload: String) : SignalPayload(), IConvertible
data class payloadInt(private var payload: Int) : SignalPayload(), IConvertible
data class payloadFloat(private var payload: Float) : SignalPayload(), IConvertible
data class payloadBool(private var payload: Boolean) : SignalPayload(), IConvertible {

    var highLevel = 1.0
    var lowLevel = 0.0
    var trueState: String = true.toString()
    var falseState: String = false.toString()

    override fun setPayload(data: IConvertible) {
        payload = when (data) {
            is payloadBool -> data.asBool
            is payloadInt, is payloadFloat -> when {
                data.asFloat >= highLevel -> true
                data.asFloat <= lowLevel -> false
                else -> return
            }
            is payloadString -> when (data.asString) {
                trueState -> true
                falseState -> false
                else -> return
            }
            else -> data.asBool
        }
    }

    override val asBool
        get() = payload

    override val asInt
        get() = if (payload) highLevel.toInt() else lowLevel.toInt()

    override val asFloat
        get() = if (payload) highLevel.toFloat() else lowLevel.toFloat()

    override val asString
        get() = if (payload) trueState else falseState
}


class SignalChannel(val idx: Int, val type: SignalType, val name: String = "") : IPublishing, IArriving {
    @Volatile var payload: SignalPayload = Empty // TODO Конкурентный ресурс
    var changeDataWhenPublish = false

    var publishCallback: ((data: SignalPayload?, signal: IPublishing?) -> Unit)? =  null

    var publishListener: IPublishing? = null
    val arrivingDataEventManager = ArrivingDataEventManager()

    fun notifyListeners(data: SignalPayload){
        arrivingDataEventManager.notifyListeners(data ,this)
    }

    override fun publish(data: SignalPayload, signal: IPublishing?) {
        publishCallback?.invoke(data, signal)
        if (changeDataWhenPublish) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        publishListener?.publish(data, this)
    }

    override fun onNewPayload(data: SignalPayload, sender: IArriving?) {
        synchronized(this, )
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface IConvertible {
    val asBool: Boolean
    val asInt: Int
    val asFloat: Float
    val asString: String
    fun setPayload(data: IConvertible)
}

/**
 * Для отправки значения на сервер
 */
interface IPublishing {
    fun publish(data: SignalPayload, signal: IPublishing?)
}

/**
 * Прием данных извне
 */
interface IArriving {
    fun onNewPayload(data: SignalPayload, sender: IArriving?)
}