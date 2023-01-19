package com.example.sunmiprinterethernetbridge

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.sunmi.peripheral.printer.*

class PrinterManager {

    private val printerCallback: InnerPrinterCallback = object:InnerPrinterCallback() {
        override fun onConnected(service: SunmiPrinterService?) {
            this@PrinterManager.service = service
        }

        override fun onDisconnected() {
            service = null
        }
    }

    private val resultCallback: InnerResultCallback = object:InnerResultCallback() {
        override fun onRunResult(isSuccess: Boolean) {
            Log.d(TAG, "onRunResult. $isSuccess")
        }

        override fun onReturnString(result: String?) {
            Log.d(TAG, "onReturnString. $result")
        }

        override fun onRaiseException(code: Int, msg: String?) {
            Log.d(TAG, "onRaiseException. $code, $msg")
        }

        override fun onPrintResult(code: Int, msg: String?) {
            Log.d(TAG, "onPrintResult. $code $msg")
        }
    }

    private var service: SunmiPrinterService? = null

    fun connect(context: Context) {
        try {
            InnerPrinterManager.getInstance().bindService(context, printerCallback)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    fun disconnect(context: Context) {
        try {
            InnerPrinterManager.getInstance().unBindService(context, printerCallback)
            InnerPrinterManager.getInstance().unBindService(context, printerCallback)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    fun sendRawData(data: ByteArray) {
        if (service == null) {
            Log.d(TAG, "null service.")
            return
        }
        if (data.isEmpty()) {
            Log.d(TAG, "null data.")
            return
        }

        try {
            service!!.sendRAWData(data, resultCallback)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun testPrint() {
        val hello = "Hello, world!".toByteArray()
        val japanese = "こんにちは、世界！".toByteArray(Charsets.UTF_8)
        val testData = CHINISE_ON + UTF8 + ALIGN_CENTER + hello + NEWLINE + japanese + NEWLINE + NEWLINE + END_FEED
        sendRawData(testData)
    }

    companion object {
        const val TAG = "PrinterManager"

        const val NUL: Byte = 0x00
        const val LF: Byte = 0x0a
        const val CR: Byte = 0x0d
        const val ESC: Byte = 0x1b
        const val FS: Byte = 0x1c
        const val GS: Byte = 0x1d

        val CHINISE_ON = byteArrayOf(FS, 0x26)
        val UTF8 = byteArrayOf(FS, 0x43, 0xff.toByte())
        val NEWLINE = byteArrayOf(CR, LF)
        val ALIGN_CENTER = byteArrayOf(ESC, 0x61, 0x01)
        val END_FEED = NEWLINE + NEWLINE + NEWLINE
    }
}