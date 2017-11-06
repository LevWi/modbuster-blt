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

enum class Quality(val code : Int){
    ND(0), // нет данных
    GOOD(1),
    BAD(-1)
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


abstract class Signal(val idx: Int, val mType: SignalType) : IDataConversions, RefreshSignalListener

class SignalInt(idx: Int) : Signal(idx, SignalType.INT)


