package com.lfom.services

import com.lfom.signals.Signal

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.lang.reflect.Array

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by gener on 03.01.2018.
 */

class SignalsDataService : Service() {

    private val mSigDataServiceBinder = SigDataServiceBinder()

    /*TODO
    Клиент для MQTT сервера
    ++++ массив подключений к клиенту. Можно ли реализовать несколько подключений к сервису? для разных серверов
    На каждое подключение должен быть свой массив пар (Топик, ссылка на сигнал)

    (--- это не нужно. Уже сделано в бибилиотеке) прием шировещатильных сообщение от сервиса Mqtt с соответсвующим
    обновление соотвествующего сообщения

    + проверка на уникальность Id сигнала
     */

    var mSignals : Map<Int, Signal> = ConcurrentHashMap()
        private set



    inner class SigDataServiceBinder : Binder() {
        val service: SignalsDataService
            get() = this@SignalsDataService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mSigDataServiceBinder
    }
}

