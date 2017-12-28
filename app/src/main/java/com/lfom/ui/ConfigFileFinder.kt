package com.lfom.ui

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.content.Intent
import android.support.v7.app.AlertDialog

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import com.lfom.modbuster.R
import java.io.IOException


class ConfigFileFinder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_file_finder)
    }

    private val FIND_FILE: Int = 0xA


    public fun onFindFile(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FIND_FILE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FIND_FILE && resultCode == Activity.RESULT_OK) {
            val extFile = File(data?.data?.path ?: return)
            extFile.name
            AlertDialog.  //Todo Преобразовать в фрагмент
            return
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Throws(IOException::class)
    fun copyFile(src: File, dst: File) {
        val inChannel = FileInputStream(src).channel
        val outChannel = FileOutputStream(dst).channel
        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } finally {
            inChannel?.close()
            outChannel?.close()
        }
    }
}
