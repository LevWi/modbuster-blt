package com.lfom.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.lfom.modbuster.R
import java.io.File


class ConfigFileFinder : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_file_finder)
    }

    private val FIND_FILE: Int = 0xA


    public fun onFindFile(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FIND_FILE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FIND_FILE && resultCode == Activity.RESULT_OK) {
            Log.w(TAG, data?.data?.path )
            val inpFile = File(data?.data?.path ?: return)

            if (!inpFile.canRead()) {
                Log.e(TAG, "Can't read file ${inpFile.absolutePath}")
            }
            try {
                val newFile = inpFile.copyTo(File(filesDir.path, inpFile.name), false)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
/*
    @Throws(IOException::class)
    fun copyFile(src: File, dst: File) {
        src.copyTo()
        val inChannel = FileInputStream(src).channel
        val outChannel = FileOutputStream(dst).channel
        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } finally {
            inChannel?.close()
            outChannel?.close()
        }
    }*/
}
