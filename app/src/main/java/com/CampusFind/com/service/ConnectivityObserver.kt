package com.CampusFind.com.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ConnectivityObserver(
    context: Context,
    private val onConnectionChanged: (Boolean) -> Unit = {}
) {

    private val connectivityManager = context.applicationContext
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false

    var isConnected by mutableStateOf(hasInternetConnection())
        private set

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateConnection()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateConnection()
        }

        override fun onLost(network: Network) {
            updateConnection()
        }
    }

    fun startListening() {
        if (isListening) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
        isListening = true
        isConnected = hasInternetConnection()
        onConnectionChanged(isConnected)
    }

    fun stopListening() {
        if (!isListening) return

        isListening = false
        connectivityManager.unregisterNetworkCallback(networkCallback)
        handler.removeCallbacksAndMessages(null)
    }

    private fun updateConnection() {
        handler.post {
            if (isListening) {
                val connected = hasInternetConnection()
                if (isConnected != connected) {
                    isConnected = connected
                    onConnectionChanged(connected)
                }
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
