package com.example.huellario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.huellario.data.remote.TokenManager
import com.example.huellario.navigation.HuellarioNavGraph
import com.example.huellario.ui.theme.HuellarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar TokenManager y vincularlo inmediatamente con RetrofitClient
        TokenManager.getInstance(applicationContext)

        enableEdgeToEdge()
        setContent {
            HuellarioTheme {
                val navController = rememberNavController()
                HuellarioNavGraph(navController = navController)
            }
        }
    }
}
