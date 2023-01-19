package com.example.sunmiprinterethernetbridge

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

interface ServerCallBack {
    fun onReceiveData(data: ByteArray)
}

class ServerManager(port: Int) {
    private val port: Int
    private var server: ServerSocket? = null


    init {
        this.port = port
    }

    suspend fun start(callback: ServerCallBack) = withContext(Dispatchers.IO) {
        try {
            if (server != null) {
                Log.d(TAG, "already started.")
                return@withContext
            }
            val server = ServerSocket(port)
            server.reuseAddress = true
            this@ServerManager.server = server
            Log.d(TAG, "Server start")
            while (true) {
                val socket = server.accept()
                try {
                    val inputStream = socket.getInputStream()
                    val data = inputStream.readBytes()
                    callback.onReceiveData(data)
                } catch (e: IOException) {
                    socket.close()
                }
            }
        } catch (e: SocketException) {
            Log.d(TAG, "Server stopped")
        } catch (e: IOException) {
            e.printStackTrace()
            server?.close()
        }
    }

    fun stop() {
        server?.close()
        server = null
    }

    companion object {
        const val TAG = "ServerManager"
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }