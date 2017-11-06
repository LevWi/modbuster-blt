package com.lfom.signals


/**
 * Created by gener on 04.11.2017.
 */


interface RefreshSignalListener {
    fun update(obj : IDataConversions)
}

interface IDataConversions {
    var quality : Quality

    fun getBool(): Boolean?
    fun getByte() : Byte?
    fun getShort(): Short?
    fun getInt(): Int?
    fun getFloat(): Float?
    fun getByteArray(): ByteArray?
    fun getString() : String?

    /*TODO реализовывать методы c аргументами переменной длины Vararg ?
    * Наверно все же все по простому
    * */
    fun setFromBool(value: Boolean?)
    fun setFromByte(value: Byte?)
    fun setFromShort(value: Short?)
    fun setFromInt(value: Int?)
    fun setFromFloat(value: Float?)
    fun setFromByteArray(value: ByteArray?)
    fun setFromString(value: String?)
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
// todo Убрать или оставить?
enum class Quality(val code : Int) {
    ND(2), // нет данных
    GOOD(0),
    BAD(1)
}

abstract class Signal(val idx: Int, val mType: SignalType) : IDataConversions, RefreshSignalListener {
    // todo Убрать или оставить?
    companion object {
        const val BAD_QUALITY_CODE: Int = 1
        const val GOOD_QUALITY_CODE: Int = 0
    }
}

class SignalInt(idx: Int) : Signal(idx, SignalType.INT)


