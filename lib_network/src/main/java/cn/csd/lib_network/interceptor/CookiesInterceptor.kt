package cn.csd.lib_network.interceptor

import cn.csd.lib_network.manager.CookiesManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 请求体添加 cookie, 响应体读取 cookies
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
class CookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()

        val url = request.url.toString()
        val host = request.url.host

        // 给有需要的接口添加Cookies
//        if () {
//            val cookies = CookiesManager.getCookies()
//            if (!cookies.isNullOrEmpty()) {
//                newBuilder.addHeader(KEY_COOKIE, cookies)
//            }
//        }

        // 传给下一个拦截器
        val response = chain.proceed(newBuilder.build())


        // 读取 cookies
//        if () {
//            val cookies = response.headers(KEY_SET_COOKIE)
//            val cookiesStr = CookiesManager.encodeCookie(cookies)
//            CookiesManager.saveCookies(cookiesStr)
//        }
        return response
    }
}