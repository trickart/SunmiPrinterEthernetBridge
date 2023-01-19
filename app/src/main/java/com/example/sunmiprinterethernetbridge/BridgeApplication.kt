package com.example.sunmiprinterethernetbridge

import android.app.Application
import android.util.Log

class BridgeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate!!!!!!!!!")
        printerManager.connect(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        Log.d(TAG, "Terminate!!!!!")
        printerManager.disconnect(this)
    }

    companion object {
        const val TAG = "BridgeApplication"
        val serverManager = ServerManager(9100)
        val printerManager = PrinterManager()
    }
}