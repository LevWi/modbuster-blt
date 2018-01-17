package com.lfom.modbuster

//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
import com.lfom.services.GroupSignals

import org.junit.Test


/**
 * Created by gener on 14.01.2018.
 */

class KotlinTest {


    @Test
    fun fun1() {
        val set = mutableSetOf<GroupSignals>()
        set.add(GroupSignals(0).also { it.name = "fdsfsdf" })
        println("Size of ${set.size}")
        println(set.add(GroupSignals(1).also { it.name = "fdsfsdf" }))
        println("Size of ${set.size}")
        set.add(GroupSignals(1).also { it.name = "fdsfsdf1" })
        println("Size of ${set.size}")
        println(set.add(GroupSignals(1).also { it.name = "fdsfsdf2" }))
        println("Size of ${set.size}")
    }
}