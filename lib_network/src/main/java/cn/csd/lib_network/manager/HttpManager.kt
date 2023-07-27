package cn.csd.lib_network.manager

import android.util.Log
import cn.csd.lib_framework.helper.AppHelper
import cn.csd.lib_network.api.ApiService
import cn.csd.lib_network.error.ERROR
import cn.csd.lib_network.error.NoNetWorkException
import cn.csd.lib_network.interceptor.CookiesInterceptor
import cn.csd.lib_network.interceptor.HeaderInterceptor
import cn.csd.lib_network.utill.NetWorkUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 负责管理 retrofit 的 client 初始化，以及 ApiService 的动态代理
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
object HttpManager {

    private val mRetrofit = Retrofit.Builder()
        .client(initOkHttpClient())
        .baseUrl("") // baseUrl 应该属于 constants 文件
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * 动态代理
     * */
    fun <T> create(apiService: Class<T>): T {
        return mRetrofit.create(apiService)
    }

    private fun initOkHttpClient(): OkHttpClient {
        val build = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        //日志拦截器
        val logInterceptor = HttpLoggingInterceptor { message: String ->
            Log.i("okhttp", "data:$message")
        }
        if (AppHelper.isDebug()) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        }

        // 添加拦截器
        build.addInterceptor(CookiesInterceptor())
        build.addInterceptor(HeaderInterceptor())
        build.addInterceptor(logInterceptor)
        //网络状态拦截
        build.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                if (NetWorkUtils.isConnected()) {
                    val request = chain.request()
                    return chain.proceed(request)
                } else {
                    throw NoNetWorkException(ERROR.NETWORK_ERROR)
                }
            }
        })
        return build.build()
    }

}