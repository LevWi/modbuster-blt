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





// TODO Сделать канал на чтение и на запись ?
/*Для этого добавить:
- Событие/канал приема сообщениий Arrived event от внешнего сервера
+ сделать пустой преобразователь сигнала
+ Для публикации сделать некий объект "команда" или "значение" . Объект обвертка
+ Интерфейс "Конвертируемый"*/

abstract class Signal(val idx: Int, val mType: SignalType) :
        IDataOut,
        IDataIn,
        RefreshSignalListener {

    override var quality: Quality = Quality.ND

    abstract val byteMixer: BytesMixer?

    //val event = RefreshSignalEventManager()

    val publishEvent
    val arrivedEvent

    var name: String = ""

}




class SignalFloat(idx: Int,
                  override val byteMixer: BytesMixer? = null) : Signal(idx, SignalType.FLOAT) {
    companion object {
        const val BYTE_SIZE = java.lang.Float.SIZE / java.lang.Byte.SIZE
    }

    var value: Float? = null

    override fun update(obj: IDataOut) {
        value = obj.getFloat()
        quality = obj.quality

        event.notifyListeners(this)
    }

    override fun setData(newVal: Any?) {
        value = when (newVal) {
            null -> {
                quality = Quality.BAD
                null
            }
            is Boolean -> {
                quality = Quality.GOOD
                if (newVal) 1f else 0f
            }
            is Byte -> {
                quality = Quality.GOOD
                newVal.toFloat()
            }
            is Short -> {
                quality = Quality.GOOD
                newVal.toFloat()
            }
            is Int -> {
                quality = Quality.GOOD
                newVal.toFloat()
            }
            is Float -> {
                quality = Quality.GOOD
                newVal
            }
            is ByteArray -> {
                try {
                    quality = Quality.GOOD
                    val buff: ByteArray? = if (byteMixer != null) byteMixer.mixBytes(newVal) else newVal
                    java.nio.ByteBuffer
                            .wrap(buff)
                            .float
                } catch (e: java.nio.BufferUnderflowException) {
                    quality = Quality.BAD_ARGUMENT
                    null
                }
            }
            is String -> {
                try {
                    newVal.toFloat()
                } catch (e: NumberFormatException) {
                    quality = Quality.BAD_ARGUMENT
                    null
                }
            }
            else -> {
                quality = Quality.BAD_ARGUMENT; null
            }
        }
        event.notifyListeners(this)
    }

    override fun getBool(): Boolean? {
        return value ?: value != 0f
    }

    override fun getByte(): Byte? {
        return value?.toByte()
    }

    override fun getShort(): Short? {
        return value?.toShort()
    }

    override fun getInt(): Int? {
        return value?.toInt()
    }

    override fun getFloat(): Float? {
        return value
    }

    override fun getByteArray(): ByteArray? {
        return java.nio.ByteBuffer
                .allocate(BYTE_SIZE)
                .putFloat(value ?: return null).array()
    }

    override fun getString(): String? {
        return value?.toString()
    }
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

        event.notifyListeners(this)
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
                            .wrap(buff)
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
        event.notifyListeners(this)
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
