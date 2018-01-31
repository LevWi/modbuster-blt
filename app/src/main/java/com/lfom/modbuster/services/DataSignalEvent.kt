package com.lfom.modbuster.services

import com.lfom.modbuster.signals.SignalPayload

class DataSignalEvent (
        val idSender : Int = 0,
        val payload : SignalPayload?
)
