package com.lfom.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.lfom.modbuster.R

// TODO есть ли возможность иссползовать? import kotlinx.android.synthetic.main.group_signals_card.*

const val TYPE_GROUP: Int = 1
const val TYPE_SIGNAL: Int = 2

class SignalsItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemViewType(position: Int): Int {
        TODO("not implemented")
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

    val itemView: View
        get() = super.itemView

    val name: TextView

    //TODO Реализовать дополнительные поля

    init {
        name = view.findViewById(R.id.group_name)
    }
}

