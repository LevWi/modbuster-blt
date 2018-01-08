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

sealed class SignalPayload {
    companion object {
        /**
         * Returns SignalPayload object which implement IConvertible
         */
        fun createInstance(type: SignalType): SignalPayload {
            TODO("Нет реализации конкретных настроек подкласса")
            return when (type) {
                SignalType.BOOL -> payloadBool()
                SignalType.INT -> payloadInt()
                SignalType.FLOAT -> payloadFloat()
                SignalType.STRING -> payloadString()
            }
        }
    }

    //    object Empty : SignalPayload()
    data class BadData(val message: String = "BAD") : SignalPayload() {
        override fun toString(): String {
            return message
        }
    }

    data class payloadString(private var payload: String = "") : SignalPayload(), IConvertible
    data class payloadInt(private var payload: Int = 0) : SignalPayload(), IConvertible
    data class payloadFloat(private var payload: Float = 0F) : SignalPayload(), IConvertible
    data class payloadBool(private var payload: Boolean = false) : SignalPayload(), IConvertible {

        var highLevel = 1.0
        var lowLevel = 0.0
        var trueState: String = true.toString()
        var falseState: String = false.toString()

        override fun setFromPayload(data: IConvertible) {
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


    class SignalChannel(val idx: Int, val type: SignalType) : IPublishing, IArriving {
        var payload: SignalPayload? = null
            private set
        var name: String = ""

        var refreshDataWhenPublish = false

        var publishCallback: ((data: SignalPayload, sender: IPublishing?) -> Unit)? = null
        var arrivedCallback: ((data: SignalPayload, sender: IArriving?) -> Unit)? = null

        var publishListener: IPublishing? = null
        val arrivingDataEventManager = ArrivingDataEventManager()

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
            setInnerPayload(data)
            arrivedCallback?.invoke(data, sender)
            notifyListeners(payload ?: return)
        }

        fun setInnerPayload(data: SignalPayload) {
            //if (data != null) {
            synchronized(this) {
                when (data) {
                    is IConvertible -> {
                        if (payload == null || payload is BadData) {
                            payload = SignalPayload.createInstance(type)
                        }
                        (payload as IConvertible).setFromPayload(data)
                    }
                    is BadData -> this.payload = data.copy()
                }
            }
            //}
        }
    }
}


interface IConvertible {
    val asBool: Boolean
    val asInt: Int
    val asFloat: Float
    val asString: String
    fun setFromPayload(data: IConvertible)
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