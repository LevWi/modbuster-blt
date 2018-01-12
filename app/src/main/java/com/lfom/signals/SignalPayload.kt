package com.lfom.signals

/**
 * Created by gener on 06.01.2018.
 */

interface IConvertible {
    fun asBool(options: CreatorVariant?, reverse : Boolean = false): Boolean?
    fun asInt(options: CreatorVariant?, reverse : Boolean = false): Int?
    fun asFloat(options: CreatorVariant?, reverse : Boolean = false): Float?
    fun asString(options: CreatorVariant?, reverse : Boolean = false): String
    fun setFromPayload(data: IConvertible, reverse : Boolean = false): Boolean
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
            if (writeble && value != 0.0) field = value
        }
    var lowLevel = 0.0
        set(value) {
            if (writeble) field = value
        }
    var multiplier = 1.0
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

data class BadData(val message: String = "BAD") : SignalPayload() {
    companion object {
        const val CONVERSION_ERROR = "CONVERSION_ERROR"
    }
}

data class BoolPayload(private val boolOptions: BoolOptions, private var value: Boolean = false) : SignalPayload(), IConvertible {

    override fun asBool(options: CreatorVariant?, reverse : Boolean): Boolean? {
        return value
    }

    override fun asInt(options: CreatorVariant?, reverse : Boolean): Int? {
        val opt  = if (reverse) boolOptions else options
        return value.toDouble(opt ?: DefaultOptions).toInt()
    }

    override fun asFloat(options: CreatorVariant?, reverse : Boolean ): Float? {
        val opt  = if (reverse) boolOptions else options
        return value.toDouble(opt ?: DefaultOptions).toFloat()
    }

    override fun asString(options: CreatorVariant? , reverse : Boolean ): String {
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




data class IntPayload(private val intOptions: IntOptions,
                      private var value: Int = 0) : SignalPayload(), IConvertible {
    override fun asBool(options: CreatorVariant?, reverse : Boolean): Boolean? {
        val opt  = if (reverse) intOptions else options
        return value.toBool(opt ?: DefaultOptions)
    }

    override fun asInt(options: CreatorVariant?, reverse : Boolean): Int {
        val opt : CreatorVariant = if (reverse) intOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> (value - opt.shift) / opt.multiplier
            false -> value * opt.multiplier + opt.shift
        }.toInt()
    }

    override fun asFloat(options: CreatorVariant?, reverse : Boolean): Float? {
        return asInt(options, reverse).toFloat()
    }

    override fun asString(options: CreatorVariant?, reverse : Boolean): String {
        return asInt(options, reverse).toString()
    }

    override fun setFromPayload(data: IConvertible, reverse : Boolean): Boolean {
        value = data.asInt(intOptions, reverse) ?: return false
        return true
    }
}




data class FloatPayload(private val floatOptions: FloatOptions,
                        private var value: Float = 0F) : SignalPayload(), IConvertible {
    override fun asBool(options: CreatorVariant?, reverse : Boolean): Boolean? {
        val opt  = if (reverse) floatOptions else options
        return value.toBool(opt ?: DefaultOptions)
    }

    override fun asInt(options: CreatorVariant?, reverse: Boolean): Int {
        return asFloat(options).toInt()
    }

    override fun asFloat(options: CreatorVariant?, reverse: Boolean): Float {
        val opt : CreatorVariant = if (reverse) floatOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> (value - opt.shift) / opt.multiplier
            false -> value * opt.multiplier + opt.shift
        }.toFloat()
    }

    override fun asString(options: CreatorVariant?, reverse: Boolean): String {
        return asFloat(options).toString()
    }

    override fun setFromPayload(data: IConvertible, reverse: Boolean): Boolean {
        value = data.asFloat(floatOptions, reverse) ?: return false
        return true
    }
}



data class StringPayload(private val stringOptions: StringOptions,
                         private var value: String = "") : SignalPayload(), IConvertible {


    override fun asBool(options: CreatorVariant?, reverse: Boolean): Boolean? {
        val opt  = if (reverse) stringOptions else options ?: DefaultOptions
        return when (value) {
            opt.trueString -> true
            opt.falseString -> false
            else -> null
        }
    }

    override fun asInt(options: CreatorVariant?, reverse: Boolean): Int? {
        val opt  = if (reverse) stringOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> ((value.toIntOrNull() ?: return null) - opt.shift) / opt.multiplier
            false -> (value.toIntOrNull() ?: return null) * opt.multiplier + opt.shift
        }.toInt()
    }

    override fun asFloat(options: CreatorVariant?, reverse: Boolean): Float? {
        val opt  = if (reverse) stringOptions else options ?: DefaultOptions
        return when (reverse) {
            true -> ((value.toFloatOrNull() ?: return null) - opt.shift) / opt.multiplier
            false -> (value.toFloatOrNull() ?: return null) * opt.multiplier + opt.shift
        }.toFloat()
    }

    override fun asString(options: CreatorVariant?, reverse: Boolean): String {
        return value
    }

    override fun setFromPayload(data: IConvertible, reverse: Boolean): Boolean {
        value = data.asString(stringOptions, reverse )
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