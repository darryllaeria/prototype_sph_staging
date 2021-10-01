package com.proto.type.base.data.remote

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.proto.type.base.BuildConfig
import com.proto.type.base.Constants
import com.proto.type.base.data.model.AvatarModel
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.parser.AvatarParser
import com.proto.type.base.data.parser.ChatParser
import com.proto.type.base.extension.hasInternetConnection
import com.proto.type.base.manager.PrefsManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

val remoteModule = module {
    // Network modules
    single { provideHttpLogger() }
    single(named("auth")) { provideAuthInterceptor(get()) }
    single(named("state")) { provideNetworkCheckInterceptor(get()) }
    single { provideUnsafeClient(get(), get(named("state")), get(named("auth"))) }
    single { provideRetrofit(get()) }
    factory { provideUserService(get()) }
    factory { provideChatRoomService(get()) }
}


fun provideHttpLogger(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

fun provideNetworkCheckInterceptor(context: Context): Interceptor {
    return object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            if (context.hasInternetConnection()) {
                return chain.proceed(chain.request())
            }
            throw IOException()
        }
    }
}

fun provideAuthInterceptor(prefService: PrefsManager): Interceptor {
    return object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = prefService.getString(Constants.KEY_AUTH_TOKEN, "")
            if (prefService.getLong(Constants.KEY_TOKEN_EXPIRATION, 0 - (System.currentTimeMillis() / 1000)) <= Constants.PRE_EXPIRED_TIME) {
                CoroutineScope(Dispatchers.Main).launch {
//                    val result = firebaseService.refreshToken()
//                    if (result != null) {
//                        prefService.putString(Constants.KEY_AUTH_TOKEN, result.token!!)
//                        prefService.putLong(Constants.KEY_TOKEN_EXPIRATION, result.expirationTimestamp)
//                    }
                }
            }

            if (token.isNotEmpty()) {
                val originalRequest = chain.request()
                val authRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                return chain.proceed(authRequest)
            }

            return chain.proceed(chain.request())
        }
    }
}
//
//fun provideClient(httpLogger: HttpLoggingInterceptor, authInterceptor: Interceptor): OkHttpClient {
//    return OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(60, TimeUnit.SECONDS)
//        .writeTimeout(60, TimeUnit.SECONDS)
//        .addInterceptor(httpLogger)
//        .addInterceptor(authInterceptor)
//        .build()
//}

fun provideUnsafeClient(
    logger: HttpLoggingInterceptor,
    networkInterceptor: Interceptor,
    authInterceptor: Interceptor
): OkHttpClient {
    try {
        val x509 = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
        val trustCerts = arrayOf(x509)
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder().apply {
            sslSocketFactory(sslSocketFactory, x509)
            hostnameVerifier(HostnameVerifier { _, _ -> true })
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            addInterceptor(logger)
            addInterceptor(networkInterceptor)
            addInterceptor(authInterceptor)
        }.build()
    } catch (exp: Exception) {
        throw RuntimeException()
    }
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    val moshi = Moshi.Builder()
        .add(AvatarModel::class.java, AvatarParser())
        .add(ChatModel::class.java, ChatParser())
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}

fun provideUserService(retrofit: Retrofit): UserService {
    return retrofit.create(UserService::class.java)
}

fun provideChatRoomService(retrofit: Retrofit): ChatService {
    return retrofit.create(ChatService::class.java)
}