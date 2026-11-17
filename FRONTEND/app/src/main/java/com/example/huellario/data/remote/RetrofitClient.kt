package com.example.huellario.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton de configuración para el cliente de red Retrofit.
 */
object RetrofitClient {

    private var tokenProvider: (() -> String?)? = null

    /**
     * Registra el proveedor del token JWT para adjuntar automáticamente la cabecera
     * `Authorization: Bearer <token>` en las peticiones a endpoints protegidos.
     */
    fun setTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val token = tokenProvider?.invoke()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(ApiConfig.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val authApiService: AuthApiService
        get() = retrofit.create(AuthApiService::class.java)

    val healthApiService: HealthApiService
        get() = retrofit.create(HealthApiService::class.java)

    val petApiService: PetApiService
        get() = retrofit.create(PetApiService::class.java)

    val userApiService: UserApiService
        get() = retrofit.create(UserApiService::class.java)

    val routineApiService: RoutineApiService
        get() = retrofit.create(RoutineApiService::class.java)
}
