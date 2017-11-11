package com.lfom.signals

/**
 * Created by gener on 11.11.2017.
 */

/*todo Нужно сделать правильную проверку шаблона на минимальное значение*/
class BytesMixer(private val template: IntArray) {
    fun mixBytes(arrIn: ByteArray): ByteArray? {
        if ((template.max() ?: return arrIn) >= arrIn.lastIndex) {
            return template.map { arrIn[it] }.toByteArray()
        }
        return null
    }
}