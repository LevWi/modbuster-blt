package com.lfom.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lfom.modbuster.R
import com.lfom.services.SignalsDataService

// TODO есть ли возможность иссползовать? import kotlinx.android.synthetic.main.group_signals_card.*

const val TYPE_GROUP: Int = 1
const val TYPE_SIGNAL: Int = 2

class SignalsItemAdapter(private val connection : SignalsDataServiceConnection) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_GROUP ->  GroupSignalsViewHolder.create(parent!!)
            TYPE_SIGNAL -> SignalViewHolder.create(parent!!)
            else -> throw IllegalArgumentException("Don't know type $viewType")
        }
    }

    override fun getItemCount(): Int {
        with(connection.service ?: return 0){
            return signals.size + groups.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val srv = connection.service ?: return
        when(holder){
            is GroupSignalsViewHolder -> {
                holder.name.text = srv.groups.elementAt(position).name
            }
            is SignalViewHolder -> {
                holder.name.text = srv.signals.values.elementAt(position).name
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val srv = connection.service ?: return -1
        return when{
            position < srv.groups.size  -> TYPE_GROUP
            position < srv.signals.size -> TYPE_SIGNAL
            else -> -1
        }
    }
}


class GroupSignalsViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): GroupSignalsViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.group_signals_card, parent, false)
            return GroupSignalsViewHolder(view)
        }
    }

    val view: View
        get() = super.itemView

    val name: TextView = view.findViewById(R.id.group_name)

    //TODO Реализовать дополнительные поля

}

class SignalViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup): SignalViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.signal_card, parent, false)
            return SignalViewHolder(view)
        }
    }

    val view: View
        get() = super.itemView

    val name: TextView = view.findViewById( R.id.signal_name )

    //TODO Реализовать дополнительные поля

}

interface SignalsDataServiceConnection {
    val service : SignalsDataService?
}