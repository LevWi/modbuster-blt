package com.lfom.services


import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.lfom.signals.*
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


/**
 * Created by gener on 03.01.2018.
 */


class SignalsDataService : Service() {

    private val mSigDataServiceBinder = SigDataServiceBinder()

    /*TODO
    + проверка на уникальность Id сигнала

     TODO
     Если новое значение сигнала приходит от сервера
     - стандартное действие  , уведомляем подписанных приемников
     Пишем значение сами
     - другое действие, уведомляем подписанных приемников


     TODO Загрузка файла конфигурации

     */

    var status = StatusService.NOT_READY
        private set

    val signals: MutableMap<Int, SignalChannel> = ConcurrentHashMap()

    val mqttClients = mutableListOf<MqttClientHelper>()

    inner class SigDataServiceBinder : Binder() {
        val service: SignalsDataService
            get() = this@SignalsDataService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mSigDataServiceBinder
    }


    fun loadConfig() {
        //Log.i(MAIN_DATA_SERVICE_TAG, "Numbers of elements ${mqttClients.size}")
        status = StatusService.READ_CONFIG
        try {
            val jsonString: String = openFileInput("default.prj").use {
                it.bufferedReader().readText()
            }

            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    //.add(MqttAndroidClientJsonAdapter(applicationContext))
                    .add(MqttConnectOptionsJsonAdapter())
                    .build()

            val adapter = moshi.adapter(ServiceConfig::class.java)

            val testConfig = adapter.fromJson(jsonString)

            testConfig?.let {
                signals.putAll(it.signals)
                this.mqttClients.clear()
                it.mqttClients.forEach {
                    this.mqttClients.add(
                            MqttClientHelper.create(applicationContext, it)
                    )
                }
                Log.i(MAIN_DATA_SERVICE_TAG, "Config loaded")
            }

        } catch (ex: FileNotFoundException) {
            Log.e(MAIN_DATA_SERVICE_TAG, "File default.prj not found")
            status = StatusService.CONFIG_NOT_FOUND
        } catch (ex: IOException) {
            Log.e(MAIN_DATA_SERVICE_TAG, "Error when reading config", ex)
            status = StatusService.ERROR_CONFIG
        }
    }

    fun createLinks() {

    }

    fun startWork() {

        val serverUri = "tcp://192.168.10.11:1883"

        val clientId = "ExampleAndroidClient" + System.currentTimeMillis()
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false

        val mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId)

        val newSignal = SignalChannel(
                33,
                IntOptions()
        ).also {
            it.name = "WB_Rele_1"
            it.arrivedCallback = { data, _, _ ->
                val string = (data as? IConvertible)?.asString(null)
                Log.d(MAIN_DATA_SERVICE_TAG, "Received signal ${it.name} = $string")
            }
            it.arrivingDataEventManager.listnersIds.add(44)
            it.arrivingDataEventManager.listnersIds.add(55)
        }
        signals.put(newSignal.idx, newSignal)

        val client = MqttClientHelper(mqttAndroidClient, mqttConnectOptions)

        client.addNewSignalEntry(
                MqttClientHelper.MqttSignalEntry("/devices/wb-gpio/controls/Relay_1",
                        "/devices/wb-gpio/controls/Relay_1/on"
                ).also {
                    it.receiver = newSignal
                    newSignal.publishListener = it
                }
        )

        mqttClients.add(client)
        //client.connect()
    }

    fun generateJsonFile() {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                //.add(MqttAndroidClientJsonAdapter(applicationContext))
                .add(MqttConnectOptionsJsonAdapter())
                .build()

        val adapter = moshi.adapter(ServiceConfig::class.java)

        val testConfig = ServiceConfig(
                signals,
                mqttClients.map { it.toJson() }
        )

        val str = adapter.toJson(testConfig)


        //val config = ServiceConfig(signals, mqttClients)

        //val str = adapter.toJson(config).toByteArray()

        val outFileStream = openFileOutput("default.prj", Context.MODE_PRIVATE)
        outFileStream.use { out -> out.write(str.toByteArray()) }

    }
}

enum class StatusService(code: Int) {
    CONFIG_NOT_FOUND(-2),
    ERROR_CONFIG(-1),
    NOT_READY(0),
    BUSY(1),
    READ_CONFIG(2),
    READY_TO_START(3),
    WORKING(3)
}