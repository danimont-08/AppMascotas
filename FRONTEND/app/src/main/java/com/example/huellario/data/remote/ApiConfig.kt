package com.example.huellario.data.remote

object ApiConfig {

    enum class NetworkMode {
        EMULATOR,     // Emulador estándar de Android Studio (10.0.2.2)
        LOCAL_WIFI,   // Celular físico conectado a la misma red Wi-Fi
        HOTSPOT,      // Hotspot de Windows

    }

    // MODO DE RED:
    var currentMode: NetworkMode = NetworkMode.HOTSPOT


    var wifiIp: String = "192.168.66.172"


    var hotspotIp: String = "192.168.137.1"

    private const val PORT = "3000"

    val baseUrl: String
        get() = when (currentMode) {
            NetworkMode.EMULATOR -> "http://10.0.2.2:$PORT/api/"
            NetworkMode.LOCAL_WIFI -> "http://$wifiIp:$PORT/api/"
            NetworkMode.HOTSPOT -> "http://$hotspotIp:$PORT/api/"

        }


    fun resolveImageUrl(photoUrl: String?): String? {
        if (photoUrl.isNullOrBlank()) return null
        val baseWithoutApi = baseUrl.removeSuffix("api/")
        return if (photoUrl.startsWith("http")) {
            photoUrl
                .replace("http://localhost:3000", baseWithoutApi.removeSuffix("/"))
                .replace("http://127.0.0.1:3000", baseWithoutApi.removeSuffix("/"))
                .replace("https://localhost:3000", baseWithoutApi.removeSuffix("/"))
        } else {
            "${baseWithoutApi.removeSuffix("/")}/${photoUrl.removePrefix("/")}"
        }
    }
}
