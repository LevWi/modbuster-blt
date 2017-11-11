package com.lfom.signals


/**
 * Created by gener on 04.11.2017.
 */


interface IDataOut {
    var quality: Quality

    fun getBool(): Boolean?
    fun getByte(): Byte?
    fun getShort(): Short?
    fun getInt(): Int?
    fun getFloat(): Float?
    fun getByteArray(): ByteArray?
    fun getString(): String?
    fun getStringPerformance(): String {
        return "${getString()}"
    }
}

/*
interface IDataIn {
    fun setData(value: Boolean?)
    fun setData(value: Byte?)
    fun setData(value: Short?)
    fun setData(value: Int?)
    fun setData(value: Float?)
    fun setData(value: ByteArray?)
    fun setData(value: String?)
}*/

interface IDataIn {
    fun setData(newVal: Any?)
}

enum class SignalType {
    BOOL,
    BYTE,
    SHORT,
    INT,
    FLOAT,
    BYTE_ARR,
    STRING
}

enum class Quality(val code: Int) {
    BAD_ARGUMENT(3),
    ND(2), // нет данных
    GOOD(0),
    BAD(1)
}

// TODO Сделать канал на чтение и на запись ?
abstract class Signal(val idx: Int, val mType: SignalType) : IDataOut, IDataIn, RefreshSignalListener {

    override var quality: Quality = Quality.ND

    abstract val byteMixer: BytesMixer?

    val event = RefreshSignalEventManager()

    var name: String = ""

}


class SignalInt(idx: Int,
                override val byteMixer: BytesMixer? = null) : Signal(idx, SignalType.INT) {
    companion object {
        const val BYTE_SIZE = java.lang.Integer.SIZE / java.lang.Byte.SIZE
    }

    var value: Int? = null

    override fun update(obj: IDataOut) {
        value = obj.getInt()
        quality = obj.quality

        event.notifyListners(this)
    }

    override fun setData(newVal: Any?) {
        value = when (newVal) {
            null -> {
                quality = Quality.BAD
                null
            }
            is Boolean -> {
                quality = Quality.GOOD
                if (newVal) 1 else 0
            }
            is Byte -> {
                quality = Quality.GOOD
                newVal.toInt()
            }
            is Short -> {
                quality = Quality.GOOD
                newVal.toInt()
            }
            is Int -> {
                quality = Quality.GOOD
                newVal
            }
            is Float -> {
                quality = Quality.GOOD
                newVal.toInt()
            }
            is ByteArray -> {
                try {
                    quality = Quality.GOOD
                    val buff: ByteArray? = if (byteMixer != null) byteMixer.mixBytes(newVal) else newVal
                    java.nio.ByteBuffer
                            .wrap( buff )
                            .int
                } catch (e: java.nio.BufferUnderflowException) {
                    quality = Quality.BAD_ARGUMENT
                    null
                }
            }
            is String -> {
                try {
                    newVal.toInt()
                } catch (e: NumberFormatException) {
                    quality = Quality.BAD_ARGUMENT
                    null
                }
            }
            else -> {
                quality = Quality.BAD_ARGUMENT; null
            }
        }
        event.notifyListners(this)
    }

    override fun getBool(): Boolean? {
        return value ?: value != 0
    }

    override fun getByte(): Byte? {
        return value?.toByte()
    }

    override fun getShort(): Short? {
        return value?.toShort()
    }

    override fun getInt(): Int? {
        return value
    }

    override fun getFloat(): Float? {
        return value?.toFloat()
    }

    override fun getByteArray(): ByteArray? {
        return java.nio.ByteBuffer
                .allocate(BYTE_SIZE)
                .putInt(value ?: return null).array()
    }

    override fun getString(): String? {
        return value?.toString()
    }
}
