package com.lfom.signals

import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import com.squareup.moshi.FromJson



/**
 * Created by gener on 06.01.2018.
 */


interface IConvertible {
    fun asBool(options: PayloadOptions?, reverse : Boolean = false): Boolean?
    fun asInt(options: PayloadOptions?, reverse : Boolean = false): Int?
    fun asFloat(options: PayloadOptions?, reverse : Boolean = false): Float?
    fun asString(options: PayloadOptions?, reverse : Boolean = false): String
    fun setFromPayload(data: IConvertible, reverse : Boolean = false): Boolean
}

/*
interface IPayloadCreator{
    fun create() : SignalPayload
}*/

enum class TypePayload{
    BOOL, INT, FLOAT, STRING
}


open class PayloadOptions {

    var type: TypePayload = TypePayload.STRING

    var trueString: String = true.toString()

    var falseString: String = false.toString()

    var highLevel = 1.0

    var lowLevel = 0.0

    var multiplier = 1.0

    var shift = 0

    fun create() : SignalPayload {
        return when(type) {
            TypePayload.BOOL -> BoolPayload(this)
            TypePayload.INT -> IntPayload(this)
            TypePayload.FLOAT -> FloatPayload(this)
            TypePayload.STRING -> StringPayload(this)
        }
    }
}

object DefaultOptions : PayloadOptions()


class BoolOptions : PayloadOptions() {
    init {
        type = TypePayload.BOOL
    }

}
class IntOptions : PayloadOptions()  {
    init {
        type = TypePayload.INT
    }

}
class FloatOptions : PayloadOptions()  {
    init {
        type = TypePayload.FLOAT
    }

}
class StringOptions : PayloadOptions() {
    init {
        type = TypePayload.STRING
    }
}


sealed class SignalPayload

data class BadData(val message: String = "BAD") : SignalPayload() {
    companion object {
        const val CONVERSION_ERROR = "CONVERSION_ERROR"
    }
}

data class BoolPayload(private val boolOptions: PayloadOptions, private var value: Boolean = false) : SignalPayload(), IConvertible {

    override fun asBool(options: PayloadOptions?, reverse : Boolean): Boolean? {
        return value
    }

    override fun asInt(options: PayloadOptions?, reverse : Boolean): Int? {
        val opt  = if (reverse) boolOptions else options
        return value.toDouble(opt ?: DefaultOptions).toInt()
    }

    override fun asFloat(options: PayloadOptions?, reverse : Boolean ): Float? {
        val opt  = if (reverse) boolOptions else options
        return value.toDouble(opt ?: DefaultOptions).toFloat()
    }

    override fun asString(options: PayloadOptions? , reverse : Boolean ): String {
        val opt  = if (reverse) boolOptions else options
        if (opt != null) {
            return if (value) opt.trueString else opt.falseString
        }
        return value.toString()
    }

    override fun setFromPayload(data: IConvertible, reverse : Boolean ): Boolean {
        value = data.asBool(boolOptions, reverse) ?: return false
        return true
    }
}




data class IntPayload(private val intOptions: PayloadOptions,
                      private var value: Int = 0) : SignalPayload(), IConvertible {
    override fun asBool(options: PayloadOptions?, reverse : Boolean): Boolean? {
        val opt  = if (reverse) intOptions else options
        return value.toBool(opt ?: DefaultOptions)
    }

    override fun asInt(options: PayloadOptions?, reverse : Boolean): Int {
        val opt : PayloadOptions = if (reverse) intOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> (value - opt.shift) / opt.multiplier
            false -> value * opt.multiplier + opt.shift
        }.toInt()
    }

    override fun asFloat(options: PayloadOptions?, reverse : Boolean): Float? {
        return asInt(options, reverse).toFloat()
    }

    override fun asString(options: PayloadOptions?, reverse : Boolean): String {
        return asInt(options, reverse).toString()
    }

    override fun setFromPayload(data: IConvertible, reverse : Boolean): Boolean {
        value = data.asInt(intOptions, reverse) ?: return false
        return true
    }
}




data class FloatPayload(private val floatOptions: PayloadOptions,
                        private var value: Float = 0F) : SignalPayload(), IConvertible {
    override fun asBool(options: PayloadOptions?, reverse : Boolean): Boolean? {
        val opt  = if (reverse) floatOptions else options
        return value.toBool(opt ?: DefaultOptions)
    }

    override fun asInt(options: PayloadOptions?, reverse: Boolean): Int {
        return asFloat(options).toInt()
    }

    override fun asFloat(options: PayloadOptions?, reverse: Boolean): Float {
        val opt : PayloadOptions = if (reverse) floatOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> (value - opt.shift) / opt.multiplier
            false -> value * opt.multiplier + opt.shift
        }.toFloat()
    }

    override fun asString(options: PayloadOptions?, reverse: Boolean): String {
        return asFloat(options).toString()
    }

    override fun setFromPayload(data: IConvertible, reverse: Boolean): Boolean {
        value = data.asFloat(floatOptions, reverse) ?: return false
        return true
    }
}



data class StringPayload(private val stringOptions: PayloadOptions,
                         private var value: String = "") : SignalPayload(), IConvertible {


    override fun asBool(options: PayloadOptions?, reverse: Boolean): Boolean? {
        val opt  = if (reverse) stringOptions else options ?: DefaultOptions
        return when (value) {
            opt.trueString -> true
            opt.falseString -> false
            else -> null
        }
    }

    override fun asInt(options: PayloadOptions?, reverse: Boolean): Int? {
        val opt  = if (reverse) stringOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> ((value.toIntOrNull() ?: return null) - opt.shift) / opt.multiplier
            false -> (value.toIntOrNull() ?: return null) * opt.multiplier + opt.shift
        }.toInt()
    }

    override fun asFloat(options: PayloadOptions?, reverse: Boolean): Float? {
        val opt  = if (reverse) stringOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> ((value.toFloatOrNull() ?: return null) - opt.shift) / opt.multiplier
            false -> (value.toFloatOrNull() ?: return null) * opt.multiplier + opt.shift
        }.toFloat()
    }

    override fun asString(options: PayloadOptions?, reverse: Boolean): String {
        return value
    }

    override fun setFromPayload(data: IConvertible, reverse: Boolean): Boolean {
        value = data.asString(stringOptions, reverse )
        return true
    }

}


fun Number.toBool(options: PayloadOptions): Boolean? {
    val v = this.toDouble()
    return when {
        v >= options.highLevel -> true
        v <= options.lowLevel -> false
        else -> null
    }
}



fun Boolean.toDouble(options: PayloadOptions): Double {
    return if (this) options.highLevel else options.lowLevel
}