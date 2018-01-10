package com.lfom.signals

/**
 * Created by gener on 06.01.2018.
 */

interface IConvertible {
    fun asBool(options: CreatorVariant?): Boolean?
    fun asInt(options: CreatorVariant?): Int?
    fun asFloat(options: CreatorVariant?): Float?
    fun asString(options: CreatorVariant?): String
    fun setFromPayload(data: IConvertible): Boolean
}

interface IPayloadCreator{
    fun create() : SignalPayload
}

sealed class CreatorVariant(val writeble: Boolean = false) {
    var trueString: String = true.toString()
        set(value) {
            if (writeble) field = value
        }
    var falseString: String = false.toString()
        set(value) {
            if (writeble) field = value
        }
    var highLevel = 1.0
        set(value) {
            if (writeble) field = value
        }
    var lowLevel = 0.0
        set(value) {
            if (writeble) field = value
        }
    var multiplier = 1
        set(value) {
            if (writeble) field = value
        }
    var shift = 0
        set(value) {
            if (writeble) field = value
        }
}

object DefaultOptions : CreatorVariant(false)

class BoolOptions : CreatorVariant() , IPayloadCreator {
    override fun create(): SignalPayload {
        return BoolPayload(this)
    }
}
class IntOptions : CreatorVariant() , IPayloadCreator {
    override fun create(): SignalPayload {
        return IntPayload(this)
    }
}
class FloatOptions : CreatorVariant() , IPayloadCreator {
    override fun create(): SignalPayload {
        return FloatPayload(this)
    }
}
class StringOptions : CreatorVariant() , IPayloadCreator{
    override fun create(): SignalPayload {
        return StringPayload(this)
    }
}


sealed class SignalPayload

data class BadData(val message: String = "BAD") : SignalPayload()

data class BoolPayload(private val boolOptions: BoolOptions, private var value: Boolean = false) : SignalPayload(), IConvertible {

    override fun asBool(options: CreatorVariant?): Boolean? {
        return value
    }

    override fun asInt(options: CreatorVariant?): Int? {
        return value.toDouble(options ?: DefaultOptions).toInt()
    }

    override fun asFloat(options: CreatorVariant?): Float? {
        return value.toDouble(options ?: DefaultOptions).toFloat()
    }

    override fun asString(options: CreatorVariant?): String {
        if (options != null) {
            return if (value) options.trueString else options.falseString
        }
        return value.toString()
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        value = data.asBool(boolOptions) ?: return false
        return true
    }
}




data class IntPayload(private val intOptions: IntOptions,
                      private var value: Int = 0) : SignalPayload(), IConvertible {
    override fun asBool(options: CreatorVariant?): Boolean? {
        return value.toBool(options ?: DefaultOptions)
    }

    override fun asInt(options: CreatorVariant?): Int {
        val opt = options ?: DefaultOptions
        return value * opt.multiplier + opt.shift
    }

    override fun asFloat(options: CreatorVariant?): Float? {
        return asInt(options).toFloat()
    }

    override fun asString(options: CreatorVariant?): String {
        return asInt(options).toString()
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        value = data.asInt(intOptions) ?: return false
        return true
    }
}




data class FloatPayload(private val floatOptions: FloatOptions,
                        private var value: Float = 0F) : SignalPayload(), IConvertible {
    override fun asBool(options: CreatorVariant?): Boolean? {
        return value.toBool(options ?: DefaultOptions)
    }

    override fun asInt(options: CreatorVariant?): Int {
        return asFloat(options).toInt()
    }

    override fun asFloat(options: CreatorVariant?): Float {
        val opt = options ?: DefaultOptions
        return value * opt.multiplier + opt.shift
    }

    override fun asString(options: CreatorVariant?): String {
        return asInt(options).toString()
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        value = data.asFloat(floatOptions) ?: return false
        return true
    }
}



data class StringPayload(private val stringOptions: StringOptions,
                         private var value: String = "") : SignalPayload(), IConvertible {
    override fun asBool(options: CreatorVariant?): Boolean? {
        val opt = options ?: DefaultOptions
        return when (value) {
            opt.trueString -> true
            opt.falseString -> false
            else -> null
        }
    }

    override fun asInt(options: CreatorVariant?): Int? {
        val opt = options ?: DefaultOptions
        return (value.toIntOrNull() ?: return null) * opt.multiplier + opt.shift
    }

    override fun asFloat(options: CreatorVariant?): Float? {
        val opt = options ?: DefaultOptions
        return (value.toFloatOrNull() ?: return null) * opt.multiplier + opt.shift
    }

    override fun asString(options: CreatorVariant?): String {
        return value
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        value = data.asString(stringOptions)
        return true
    }

}



fun Number.toBool(options: CreatorVariant): Boolean? {
    val v = this.toDouble()
    return when {
        v >= options.highLevel -> true
        v <= options.lowLevel -> false
        else -> null
    }
}



fun Boolean.toDouble(options: CreatorVariant): Double {
    return if (this) options.highLevel else options.lowLevel
}