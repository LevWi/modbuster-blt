package com.lfom.signals


/**
 * Created by gener on 04.11.2017.
 */



interface RefreshSignalListener{
    fun update()
}


enum class SignalType{
    BOOL,
    SHORT,
    INT ,
    FLOAT,
    BYTE_ARR,
}

abstract class Signal(val idx: Int, val mType: SignalType) : ISignalIO

class SignalInt(idx : Int) : Signal(idx, SignalType.INT) {

}



