package com.example.huellario

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.huellario.data.remote.TokenManager
import com.example.huellario.navigation.HuellarioNavGraph
import com.example.huellario.ui.theme.HuellarioTheme

class MainActivity : ComponentActivity() {

    // Android 17 (API 37) exige este permiso para conectarse a direcciones de la red local,
    // como el backend en 10.0.2.2 (emulador) o 192.168.x.x (Wi-Fi / hotspot).
    private val localNetworkPermission = "android.permission.ACCESS_LOCAL_NETWORK"

    private val requestLocalNetwork =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar TokenManager y vincularlo inmediatamente con RetrofitClient
        TokenManager.getInstance(applicationContext)

        requestLocalNetworkPermissionIfNeeded()

        enableEdgeToEdge()
        setContent {
            HuellarioTheme {
                val navController = rememberNavController()
                HuellarioNavGraph(navController = navController)
            }
        }
    }

    private fun requestLocalNetworkPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 37) return
        val granted = ContextCompat.checkSelfPermission(this, localNetworkPermission) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestLocalNetwork.launch(localNetworkPermission)
        }
    }
}
