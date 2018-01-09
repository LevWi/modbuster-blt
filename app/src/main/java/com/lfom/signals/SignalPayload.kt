package com.lfom.signals

/**
 * Created by gener on 06.01.2018.
 */

interface IConvertible {
    fun asBool(options: ConvertOptions?): Boolean?
    fun asInt(options: ConvertOptions?): Int?
    fun asFloat(options: ConvertOptions?): Float?
    fun asString(options: ConvertOptions?): String
    fun setFromPayload(data: IConvertible): Boolean
}


sealed class ConvertOptions(val writeble: Boolean = false) {
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

object DefaultOptions : ConvertOptions(false)
class BoolOptions : ConvertOptions()
class IntOptions : ConvertOptions()
class FloatOptions : ConvertOptions()
class StringOptions : ConvertOptions()


sealed class SignalPayload {
    companion object {
        /**
         * Returns SignalPayload object corresponding type of `options` argument
         */
        fun createInstance(options: ConvertOptions): SignalPayload? {
            return when (options) {
                is BoolOptions -> PayloadBool(options)
                is IntOptions -> PayloadInt(options)
                is FloatOptions -> PayloadFloat(options)
                is StringOptions -> PayloadString(options)
                else ->  null
            }
        }
    }
}




data class BadData(val message: String = "BAD") : SignalPayload()




data class PayloadBool(private val boolOptions: BoolOptions, private var payload: Boolean = false) : SignalPayload(), IConvertible {

    override fun asBool(options: ConvertOptions?): Boolean? {
        return payload
    }

    override fun asInt(options: ConvertOptions?): Int? {
        return payload.toDouble(options ?: DefaultOptions).toInt()
    }

    override fun asFloat(options: ConvertOptions?): Float? {
        return payload.toDouble(options ?: DefaultOptions).toFloat()
    }

    override fun asString(options: ConvertOptions?): String {
        if (options != null) {
            return if (payload) options.trueString else options.falseString
        }
        return payload.toString()
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        payload = data.asBool(boolOptions) ?: return false
        return true
    }
}




data class PayloadInt(private val intOptions: IntOptions,
                      private var payload: Int = 0) : SignalPayload(), IConvertible {
    override fun asBool(options: ConvertOptions?): Boolean? {
        return payload.toBool(options ?: DefaultOptions)
    }

    override fun asInt(options: ConvertOptions?): Int {
        val opt = options ?: DefaultOptions
        return payload * opt.multiplier + opt.shift
    }

    override fun asFloat(options: ConvertOptions?): Float? {
        return asInt(options).toFloat()
    }

    override fun asString(options: ConvertOptions?): String {
        return asInt(options).toString()
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        payload = data.asInt(intOptions) ?: return false
        return true
    }
}





data class PayloadFloat(private val floatOptions: FloatOptions,
                        private var payload: Float = 0F) : SignalPayload(), IConvertible {
    override fun asBool(options: ConvertOptions?): Boolean? {
        return payload.toBool(options ?: DefaultOptions)
    }

    override fun asInt(options: ConvertOptions?): Int {
        return asFloat(options).toInt()
    }

    override fun asFloat(options: ConvertOptions?): Float {
        val opt = options ?: DefaultOptions
        return payload * opt.multiplier + opt.shift
    }

    override fun asString(options: ConvertOptions?): String {
        return asInt(options).toString()
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        payload = data.asFloat(floatOptions) ?: return false
        return true
    }
}




data class PayloadString(private val stringOptions: StringOptions,
                         private var payload: String = "") : SignalPayload(), IConvertible {
    override fun asBool(options: ConvertOptions?): Boolean? {
        val opt = options ?: DefaultOptions
        return when (payload) {
            opt.trueString -> true
            opt.falseString -> false
            else -> null
        }
    }

    override fun asInt(options: ConvertOptions?): Int? {
        val opt = options ?: DefaultOptions
        return (payload.toIntOrNull() ?: return null) * opt.multiplier + opt.shift
    }

    override fun asFloat(options: ConvertOptions?): Float? {
        val opt = options ?: DefaultOptions
        return (payload.toFloatOrNull() ?: return null) * opt.multiplier + opt.shift
    }

    override fun asString(options: ConvertOptions?): String {
        return payload
    }

    override fun setFromPayload(data: IConvertible): Boolean {
        payload = data.asString(stringOptions)
        return true
    }

}



fun Number.toBool(options: ConvertOptions): Boolean? {
    val v = this.toDouble()
    return when {
        v >= options.highLevel -> true
        v <= options.lowLevel -> false
        else -> null
    }
}



fun Boolean.toDouble(options: ConvertOptions): Double {
    return if (this) options.highLevel else options.lowLevel
}