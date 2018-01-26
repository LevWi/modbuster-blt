package com.lfom.modbuster.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView

import com.lfom.modbuster.R
import com.lfom.modbuster.services.SignalsDataService

// TODO есть ли возможность иссползовать? import kotlinx.android.synthetic.main.card_group_signals.*

const val TYPE_GROUP: Int = 5
const val TYPE_SIGNAL: Int = 10
private const val TAG = "SignalsItemAdapter"



class SignalsItemAdapter(private val connection: SignalsDataServiceConnection) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            it.signals.size + it.groups.size
        } ?: 0
        Log.d(TAG, "Call getItemCount() = $count")
        return count

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val srv = connection.service ?: return
        when (holder) {
            is GroupSignalsViewHolder -> {
                holder.name.text = srv.groups.elementAt(position).name
                holder.listOfSignals.addView()
            }
            is SignalViewHolder -> {
                holder.name.text = srv.signals.values.elementAt(position - srv.groups.size).name
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //TODO
        Log.w(TAG, "Call getItemViewType() , $position ")
        val srv = connection.service ?: return -1
        val tp = when {
            position < srv.groups.size -> TYPE_GROUP
            position < srv.groups.size + srv.signals.size -> TYPE_SIGNAL
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

    val listOfSignals: ListView = view.findViewById(R.id.group_signals_listview)


}

class SignalViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): SignalViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_signal, parent, false)
            return SignalViewHolder(view)
        }
    }

    val view: View
        get() = super.itemView

    val name: TextView = view.findViewById(R.id.card_signal_name)

    val data: TextView = view.findViewById(R.id.card_signal_data)


}

interface SignalsDataServiceConnection {
    val service: SignalsDataService?
}