package com.lfom.modbuster.ui.barcode

import android.os.CountDownTimer
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lfom.modbuster.ui.SignalViewHolder
import com.lfom.modbuster.ui.SignalsDataServiceConnection
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.ConcurrentHashMap


const val LIVE_TIME: Long = 7500

class SignalsBarcodeItemAdapter(private val connection: SignalsDataServiceConnection,
                                val countInterval: Long = 150

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val tempViews : MutableMap<Int, SuicideEntry> = ConcurrentHashMap()

    val timer = object : CountDownTimer(3600000 /*TODO*/, countInterval) {
        override fun onFinish() {
            tempViews.clear()
            notifyDataSetChanged()
        }

        override fun onTick(millisUntilFinished: Long) {

            tempViews.forEach {
                it.value.timeRemaining -= countInterval

            }
            tempViews.values.removeAll {
                it.timeRemaining <= 0
            }
                    .let {
                        if (it) notifyDataSetChanged()
                        sendRefreshMessage()
                    }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return SignalViewHolder.create(parent!!)
    }

    override fun getItemCount(): Int {
        connection.service ?: return 0
        return tempViews.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val srv = connection.service ?: return

        if (holder !is SignalViewHolder) return

        val id = tempViews.keys.elementAt(position)

        srv.signals[id]?.let {
            holder.signalCard.name.text = it.name
            holder.signalCard.idSignal = id
        }
    }

    fun addNewBarcode(id: Int) {
        val result = tempViews[id]?.also {
            it.timeRemaining = LIVE_TIME
        }

        if (result == null) {
            tempViews.put(id , SuicideEntry(id))
            val index = tempViews.keys.indexOf(id)
            notifyItemChanged(index)
            sendRefreshMessage()
        }
    }

    private fun sendRefreshMessage(){
        EventBus.getDefault().post(
                EventMessage(BarcodeCaptureActivity.COMMAND_REFRESH_RECYCLERVIEW)
        )
    }

    data class SuicideEntry(val idSignal: Int) {
        var timeRemaining: Long = LIVE_TIME
    }


}

class EventMessage( val message: String)






