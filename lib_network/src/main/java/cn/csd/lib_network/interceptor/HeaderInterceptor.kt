package cn.csd.lib_network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 用于添加请求头
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
class HeaderInterceptor(
    private val headers: Map<String, String>? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()

        // 添加请求头
        headers?.let {
            for ((key, value) in it) {
                newBuilder.addHeader(key, value)
            }
        }

        return chain.proceed(newBuilder.build())
    }
}