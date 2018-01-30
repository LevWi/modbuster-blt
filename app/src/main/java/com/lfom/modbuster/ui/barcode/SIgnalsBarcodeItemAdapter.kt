package com.lfom.modbuster.ui.barcode

import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lfom.modbuster.signals.TypePayload
import com.lfom.modbuster.ui.SignalViewHolder
import com.lfom.modbuster.ui.SignalsDataServiceConnection
import org.greenrobot.eventbus.EventBus


const val LIVE_TIME: Long = 5000

class SignalsBarcodeItemAdapter(private val connection: SignalsDataServiceConnection,
                                val countInterval: Long = 150

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val tempViews : MutableMap<Int, SuicideEntry> = LinkedHashMap()

    val timer = object : CountDownTimer(3600000 /*TODO*/, countInterval) {
        override fun onFinish() {
            tempViews.clear()
            notifyDataSetChanged()
        }

        override fun onTick(millisUntilFinished: Long) {

                tempViews.forEach {
                    it.value.timeRemaining -= countInterval
                }

                tempViews.entries.forEachIndexed { index, element ->
                    if (element.value.timeRemaining <= 0) {
                        tempViews.remove(element.key)
                       notifyItemRemoved(index)
                        sendRefreshMessage()
                        return
                    }
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
            with(holder.signalCard) {
                name.text = it.name
                idSignal = id
                boolType = it.options.type == TypePayload.BOOL
                setNewData(it.payload ?: return@with)
            }
        }
    }

    fun addNewBarcode(id: Int) {
        val result = tempViews[id]?.also {
            it.timeRemaining = LIVE_TIME
        }

        if (result == null) {
            tempViews.put(id , SuicideEntry(id))
            val index = tempViews.keys.indexOf(id)

            notifyItemInserted(index)
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






