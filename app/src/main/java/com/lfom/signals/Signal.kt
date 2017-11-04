package com.lfom.signals

/**
 * Created by gener on 04.11.2017.
 */

interface ISignalIO<in I,out O> {
    fun onInputData(data : I?)
    fun outputData() : O?
}

abstract class Converter<in I, out O>(val id: Int) : ISignalIO<I, O> {

}

class ConverterByteToString(id : Int) : Converter<Byte, Int>(id){

}

class Signal<T>(val id: Int) : ISignalIO<T,T> {
    var value: T? = null

}