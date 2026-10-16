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

/** Observa la red de Android y expone un estado observable por Compose; requiere internet validado por el sistema. */
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
        /** Vuelve a comprobar internet cuando Android informa una red disponible. */
        override fun onAvailable(network: Network) {
            updateConnection()
        }

        /** Reevalúa la conexión cuando cambian las capacidades de una red, incluida su validación. */
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateConnection()
        }

        /** Recomprueba la red activa al perder una conexión; puede existir otra red disponible. */
        override fun onLost(network: Network) {
            updateConnection()
        }
    }

    /** Registra el callback una sola vez, con la API compatible con la versión de Android, y notifica el estado inicial. */
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

    /** Retira el callback y las actualizaciones pendientes del hilo principal cuando ya no se necesita observar la red. */
    fun stopListening() {
        if (!isListening) return

        isListening = false
        connectivityManager.unregisterNetworkCallback(networkCallback)
        handler.removeCallbacksAndMessages(null)
    }

    /** Publica la comprobación en el hilo principal y avisa solo cuando cambia el estado mientras el observador sigue activo. */
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

    /** Devuelve true si la red activa anuncia INTERNET y VALIDATED. Es el estado del sistema, no una prueba de disponibilidad de Firebase. */
    private fun hasInternetConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
