package com.lfom.modbuster.ui

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lfom.modbuster.R


class SignalsListFragment : Fragment() {

    private var mServiceProvider: SignalsDataServiceConnection? = null

    var recyclerView: RecyclerView? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignalsDataServiceConnection) {
            mServiceProvider = context
        }
//        else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            // TODO mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val recView = inflater
                .inflate(R.layout.fragment_signalsitem_list, container, false)
                as RecyclerView

        if (mServiceProvider != null) {
            recView.adapter = SignalsItemAdapter(mServiceProvider!!)
        }
        // TODO Set the adapter
//        if (view is RecyclerView) {
//            val context = view.getContext()
//            if (mColumnCount <= 1) {
//                view.layoutManager = LinearLayoutManager(context)
//            } else {
//                view.layoutManager = GridLayoutManager(context, mColumnCount)
//            }
//            //recyclerView.setAdapter(new MySignalsItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));
//        }
        recView.layoutManager = LinearLayoutManager(activity)

        recyclerView = recView

        return recView
    }

    override fun onDestroyView() {
        recyclerView = null
        super.onDestroyView()
    }


    override fun onDetach() {
        super.onDetach()
        mServiceProvider = null
    }
}
