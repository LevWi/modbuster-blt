package com.lfom.modbuster.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.lfom.modbuster.R
import com.lfom.modbuster.services.SignalsDataService
import com.lfom.modbuster.services.StatusService
import kotlinx.coroutines.experimental.launch


private const val TAG = "DrawerStartActivity"
const val RESULT_CODE_FIND_FILE: Int = 10

class DrawerStartActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        SignalsDataServiceConnection {


    var listFragment: SignalsListFragment? = null

    private var boundService: SignalsDataService? = null

    val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            boundService = (service as? SignalsDataService.SigDataServiceBinder)?.service

            //TODO
            addFragment()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
        }
    }


    fun addFragment() {

        if (listFragment == null) {
            listFragment = SignalsListFragment()
        }

        val trs = fragmentManager.beginTransaction()
        trs.add(R.id.content_fragment, listFragment)
        trs.commit()
    }


    override val service: SignalsDataService?
        get() = boundService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_drawer)


        val toolbar = findViewById<Toolbar>(R.id.start_toolbar)

        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        //TODO
        navigationView.menu
                .findItem(R.id.nav_menu_auto_start)
                .actionView = SwitchCompat(this).also {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                Snackbar.make(findViewById(R.id.main_content), "Not Implemented", Snackbar.LENGTH_INDEFINITE)
                        .show()
            }
        }

        navigationView.setNavigationItemSelectedListener(this)

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SignalsDataService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.drawer_menu_items, menu);
        //return true;

        return false
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu_load_conf -> {

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, RESULT_CODE_FIND_FILE)
            }
            R.id.nav_menu_start_work -> {
                //TODO
                val refrashData = {
                    Log.w(TAG, "Refresh data")
                    listFragment?.recyclerView?.adapter?.notifyDataSetChanged()
                }


                boundService?.let {
                    if (it.state == StatusService.NOT_READY) {
                        launch {
                            it.loadConfig()
                            runOnUiThread { refrashData() }
                        }
                    } else {
                        refrashData()
                    }
                }


            }
            R.id.nav_menu_connections -> {
                Snackbar.make(findViewById(R.id.main_content), "Not Implemented", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.GREEN)
                        .show()
            }
            R.id.nav_menu_settings -> {
                Snackbar.make(findViewById(R.id.main_content), "Not Implemented", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.MAGENTA)
                        .show()
            }

        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CODE_FIND_FILE && resultCode == Activity.RESULT_OK) {
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

