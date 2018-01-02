package com.lfom.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.lfom.modbuster.R


const val FIND_FILE: Int = 0xA
const val TAG = "ConfigFileFinder"

class ConfigFileFinder : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_file_finder)
    }

    public fun onFindFile(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FIND_FILE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FIND_FILE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.w(TAG, "Empty Intent for file copy")
                return
            }
            try {
                val inpFileStream = baseContext.contentResolver.openInputStream(data.data)
                val outFileStream = openFileOutput("default.prj", Context.MODE_PRIVATE)

                inpFileStream.use { input ->
                    outFileStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
