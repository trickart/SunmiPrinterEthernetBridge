package com.example.sunmiprinterethernetbridge

import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sunmiprinterethernetbridge.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val serverCallBack: ServerCallBack = (object: ServerCallBack {
        override fun onReceiveData(data: ByteArray) {
            BridgeApplication.printerManager.sendRawData(data)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupIPAddressText()
        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                start()
            } else {
                stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stop()
    }

    private fun setupIPAddressText() {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        linkProperties?.let {
            val ipAddress = it.linkAddresses.firstOrNull { it.toString().contains("192.") }
            ipAddress?.let { binding.ipAddressTextView.text = getString(R.string.ip_address_text, it) }
        }
    }

    private fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            BridgeApplication.serverManager.start(serverCallBack)
        }
    }

    private fun stop () {
        BridgeApplication.serverManager.stop()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}