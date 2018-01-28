package com.lfom.modbuster.ui.barcode

import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lfom.modbuster.ui.SignalViewHolder
import com.lfom.modbuster.ui.SignalsDataServiceConnection


class SignalsBarcodeItemAdapter(private val connection: SignalsDataServiceConnection,
                         val countInterval: Long = 150

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val tempViews = mutableSetOf<SuicideEntry>()

    val timer = object : CountDownTimer(3600000 /*TODO*/, countInterval) {
        override fun onFinish() {
            tempViews.clear()
            notifyDataSetChanged()
        }

        override fun onTick(millisUntilFinished: Long) {
            tempViews.forEach {
                it.timeRemaining -= countInterval

            }

            tempViews.removeAll {
                it.timeRemaining <= 0
            }
            notifyDataSetChanged()
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

        val id = tempViews.elementAt(position).idSignal

        srv.signals[id]?.let {
            holder.signalCard.name.text = it.name
            holder.signalCard.idSignal = id
        }
    }

    data class SuicideEntry(val idSignal: Int) {
        var timeRemaining: Long = 2500
    }
}








