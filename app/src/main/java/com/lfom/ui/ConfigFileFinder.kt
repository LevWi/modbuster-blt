package com.lfom.ui

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.lfom.modbuster.R
import android.content.Intent



class ConfigFileFinder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_file_finder)
    }

    private val FIND_FILE : Int = 0xA

    public fun onFindFile(view : View){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "file/*"
        startActivityForResult(intent, FIND_FILE)
    }

}
