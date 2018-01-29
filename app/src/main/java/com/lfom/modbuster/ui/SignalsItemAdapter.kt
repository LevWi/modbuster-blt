package com.lfom.modbuster.ui

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.lfom.modbuster.R
import com.lfom.modbuster.services.DataSignalEvent
import com.lfom.modbuster.services.SignalsDataService
import com.lfom.modbuster.signals.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


// TODO есть ли возможность иссползовать? import kotlinx.android.synthetic.main.card_group_signals.*

const val TYPE_GROUP: Int = 5
const val TYPE_SIGNAL: Int = 10
private const val TAG = "SignalsItemAdapter"


class SignalsItemAdapter(private val connection: SignalsDataServiceConnection) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val uiSignalsIds = mutableListOf<Int>()


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        //TODO
        Log.d(TAG, "Call onCreateViewHolder() , viewType = $viewType ")
        return when (viewType) {
            TYPE_GROUP -> GroupSignalsViewHolder.create(parent!!)
            TYPE_SIGNAL -> SignalViewHolder.create(parent!!)
            else -> throw IllegalArgumentException("Don't know type $viewType")
        }
    }

    override fun getItemCount(): Int {
        val count = connection.service?.let {
            uiSignalsIds.clear()
            it.signals.forEach {
                if (it.value.UIvisible) uiSignalsIds.add(it.key)
            }
            uiSignalsIds.size + it.groups.size
        } ?: 0
        Log.d(TAG, "Call getItemCount() = $count")
        return count

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val srv = connection.service ?: return
        when (holder) {
            is GroupSignalsViewHolder -> {
                srv.groups.elementAt(position).let {
                    holder.name.text = it.name
                    holder.clearSignals()
                    it.signals.forEach { key ->
                        srv.signals[key]?.let {
                            holder.addSignal(it, key)
                        }
                    }
                }
            }
            is SignalViewHolder -> {
                val idSignal = uiSignalsIds[position - srv.groups.size]
                srv.signals[idSignal]?.let {
                    holder.signalCard.name.text = it.name
                    holder.signalCard.idSignal = idSignal
                    holder.signalCard.setNewData(it.payload ?: return@let)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //TODO
        Log.w(TAG, "Call getItemViewType() , $position ")
        val srv = connection.service ?: return -1
        val tp = when {
            position < srv.groups.size -> TYPE_GROUP
            position < srv.groups.size + uiSignalsIds.size -> TYPE_SIGNAL
            else -> -1
        }
        Log.w(TAG, "Call getItemViewType() = $tp , position = $position")
        return tp
    }
}


class GroupSignalsViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): GroupSignalsViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_group_signals, parent, false)
            return GroupSignalsViewHolder(view)
        }
    }

    val view: View
        get() = super.itemView

    val name: TextView = view.findViewById(R.id.group_name)

    val groupViewSignalsCard: LinearLayout = view.findViewById(R.id.group_signals_listview)

    val listSignal: MutableList<SignalCardWrapper> = mutableListOf()


    fun addSignal(signal: SignalChannel, id: Int): SignalCardWrapper {
        val view = LayoutInflater.from(groupViewSignalsCard.context)
                .inflate(R.layout.card_signal, null, false)

        val signalViewWrapper = SignalCardWrapper(view)

        signalViewWrapper.boolType = signal.options.type == TypePayload.BOOL
        signalViewWrapper.idSignal = id
        signalViewWrapper.name.text = signal.name

        signal.payload?.let {
            signalViewWrapper.setNewData(it)
        }


        listSignal.add(signalViewWrapper)
        groupViewSignalsCard.addView(view)

        return signalViewWrapper
    }


    fun clearSignals() {
        listSignal.clear()
        groupViewSignalsCard.removeViews(0, groupViewSignalsCard.childCount)
    }
}

class SignalViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): SignalViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_signal, parent, false)
            return SignalViewHolder(view)
        }
    }

    val signalCard: SignalCardWrapper = SignalCardWrapper(view)
}


data class SignalCardWrapper(val view: View) {

    var idSignal: Int = -999

    var boolType: Boolean = false

    val name: TextView = view.findViewById(R.id.card_signal_name)

    val data: TextView = view.findViewById(R.id.card_signal_data)

    fun setDataColor(resId: Int) {
        val color = ContextCompat.getColor(view.context, resId)
        data.setTextColor(color)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewData(event: DataSignalEvent) {
        if (event.idSender == idSignal) {
            setNewData(event.payload ?: return)
        }
    }

    init {
        EventBus.getDefault().register(this)
    }

    // TODO
    protected fun finalize() {
        EventBus.getDefault().unregister(this)
    }


    fun setNewData(payload: SignalPayload) {
        when (payload) {

            is IConvertible ->
                if (boolType) {
                    payload.asBool(null, true)?.let {
                        if (it) {
                            setDataColor(R.color.TruePayload)
                            data.text = "ON"
                        } else {
                            setDataColor(R.color.FalsePayload)
                            data.text = "OFF"
                        }

                        return
                    }
                    // Ошибка конвертации
                    setDataColor(R.color.BadPayload)
                    data.text = "###"
                } else {
                    // TODO Исправить реверс данных
                    setDataColor(R.color.StandardPayload)
                    data.text = payload.asString(null, true)
                }
            is BadData -> {
                setDataColor(R.color.BadPayload)
                data.text = payload.message
            }
        }
    }


}


interface SignalsDataServiceConnection {
    val service: SignalsDataService?
}


