package com.lfom.services


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast

import com.lfom.modbuster.R
import com.lfom.signals.SignalChannel

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


class SignalsDataService : Service() {

    private val mSigDataServiceBinder = SigDataServiceBinder()


    var state = StatusService.NOT_READY
        private set


    val groups = mutableSetOf<GroupSignals>()
    val signals: MutableMap<Int, SignalChannel> = ConcurrentHashMap()
    val mqttClients = mutableListOf<MqttClientHelper>()


    inner class SigDataServiceBinder : Binder() {
        val service: SignalsDataService
            get() = this@SignalsDataService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mSigDataServiceBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mqttClients.forEach { if (it.mqttAndroidClient.isConnected) it.disconnect() }
        return super.onUnbind(intent)
    }

    fun startWork() {

        val sendWarning = { string: String ->
            Log.w(MAIN_DATA_SERVICE_TAG, string)
            showToastForUser(string)
        }
        when (state) {
            StatusService.CONFIG_NOT_FOUND -> sendWarning(resources.getString(R.string.config_not_found))
            StatusService.ERROR_CONFIG -> sendWarning(resources.getString(R.string.error_read_config))
            StatusService.NOT_READY -> {
                state = StatusService.READ_CONFIG
                loadConfig()
            }
            StatusService.READ_CONFIG -> return
            StatusService.READY_TO_START -> mqttClients.forEach { it.connect() }
            StatusService.WORKING -> return
        }
    }

    fun showToastForUser(string: String) {
        Handler(mainLooper)
                .post {
                    Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show()
                }

    }

    fun loadConfig() = runBlocking {
        clearConfig()
        try {
            val jsonString = async {
                openFileInput("default.prj").use {
                    it.bufferedReader().readText()
                }
            }
            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    //.add(MqttAndroidClientJsonAdapter(applicationContext))
                    .add(MqttConnectOptionsJsonAdapter())
                    .build()

            val adapter = moshi.adapter(ServiceConfigJson::class.java)

            val testConfig = adapter.fromJson(jsonString.await())

            testConfig?.let {
                signals.putAll(it.signals)
                this@SignalsDataService.mqttClients.clear()
                it.mqttClients.forEach {
                    this@SignalsDataService.mqttClients.add(
                            MqttClientHelper.create(applicationContext, it)
                    )
                }
                this@SignalsDataService.groups.addAll(it.groups)
                Log.i(MAIN_DATA_SERVICE_TAG, "Config loaded")
            }

            createLinks()

        } catch (ex: FileNotFoundException) {
            Log.e(MAIN_DATA_SERVICE_TAG, "File default.prj not found")
            state = StatusService.CONFIG_NOT_FOUND
        } catch (ex: IOException) {
            Log.e(MAIN_DATA_SERVICE_TAG, "Error when reading config", ex)
            state = StatusService.ERROR_CONFIG
        }
    }

    /**
     * Создание зависимостей между экземплярами MqttClientHelper и signals
     */
    private fun createLinks() {
        //Пробег по mqttClients.
        // Найти receiver_id == idx сигнала -> Связываем
        //Если publish_receiver_id == 0 -> Привязываем их и в обратную сторону, если не занят

        Log.i(MAIN_DATA_SERVICE_TAG, "Create links mqtt -> signals")
        mqttClients.forEach {
            it.mqttEntries.forEach { topic ->
                signals[topic.idxReceiver]?.let {
                    topic.receiver = it
                    if (it.publishListenerId == 0 && it.publishListener == null) {
                        it.publishListener = topic
                    }
                }
            }
        }

        //Пробег по signals
        //Если publish_receiver_id == 0 -> Привязываем их и в обратную сторону
        // Иначе ищем id среди signals и связываем
        // Игнорировать индексы приемников равные их индексам

        Log.i(MAIN_DATA_SERVICE_TAG, "Create links signals -> signals")
        signals.forEach { entry ->
            entry.value.idx = entry.key  // Todo Нужен ли вообще индекс в канале?
            with(entry.value.arrivingDataEventManager) {
                this.listnersIds.forEach { id ->
                    if (id != 0 && id != entry.key) {
                        signals[id]?.let {
                            this.subscribe(it)
                            if (it.publishListenerId == 0 && it.publishListener == null) {
                                it.publishListener = entry.value
                            }
                        }
                    }
                }
            }
            with(entry.value) {
                if (publishListenerId != 0 && publishListener == null) {
                    signals[publishListenerId]?.let {
                        this.publishListener = it
                    }
                }
            }
        }

        state = StatusService.READY_TO_START
    }

    fun clearConfig() {
        groups.clear()
        signals.clear()
        mqttClients.clear()
    }

}

enum class StatusService(code: Int) {
    CONFIG_NOT_FOUND(-2),
    ERROR_CONFIG(-1),
    NOT_READY(0),
    //BUSY(1),
    READ_CONFIG(2),
    READY_TO_START(3),
    WORKING(3)
}