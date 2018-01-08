package com.lfom.signals

/**
 * Created by gener on 06.01.2018.
 */

sealed class ConvertOprtions
class BoolOptions : ConvertOprtions() {
        companion object Defaults {
            val highLevel = 1.0
            val lowLevel = 0.0
            val trueState: String = true.toString()
            val falseState: String = false.toString()
        }
    var highLevel = 1.0
    var lowLevel = 0.0
    var trueState: String = true.toString()
    var falseState: String = false.toString()
}
class IntOptions : ConvertOprtions()
class FloatOptions : ConvertOprtions()
class StringOptions : ConvertOprtions()



sealed class SignalPayload {
    companion object {
        /**
         * Returns SignalPayload object which implement IConvertible
         */
        fun createInstance(options: ConvertOprtions): SignalPayload {
            return when (options) {
                is BoolOptions -> PayloadBool(options)
                is IntOptions -> PayloadInt(options)
                is FloatOptions -> PayloadFloat(options)
                is StringOptions -> PayloadString(options)
            }

        }
    }

    //    object Empty : SignalPayload()
    data class BadData(val message: String = "BAD") : SignalPayload() {
        override fun toString(): String {
            return message
        }
    }

    data class PayloadString(private var payload: String = "") : SignalPayload(), IConvertible
    data class PayloadInt(private var payload: Int = 0) : SignalPayload(), IConvertible
    data class PayloadFloat(private var payload: Float = 0F) : SignalPayload(), IConvertible
    data class PayloadBool(val boolOptions: BoolOptions, private var payload: Boolean = false) : SignalPayload(), IConvertible {

        override fun setFromPayload(data: IConvertible) {
            payload = when (data) {
                is PayloadBool -> data.asBool
                is PayloadInt, is PayloadFloat -> when {
                    data.asFloat >= highLevel -> true
                    data.asFloat <= lowLevel -> false
                    else -> return
                }
                is PayloadString -> when (data.asString) {
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


