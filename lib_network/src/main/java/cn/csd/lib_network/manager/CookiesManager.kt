package cn.csd.lib_network.manager

import cn.csd.lib_framework.helper.AppHelper
import io.fastkv.FastKV
import io.fastkv.FastKVConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * 管理 Cookies, 使用的是 FastKv 这个框架来替代 SP
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
object CookiesManager {

    private const val NAME_COOKIES = "kv_cookies"

    private const val KEY_COOKIES = "cookies"

    private val fastKV = FastKV.Builder(AppHelper.getApplication(), NAME_COOKIES).build()


    init {
//        FastKVConfig.setLogger() 自定义 logger
        FastKVConfig.setExecutor(Dispatchers.Default.asExecutor())
    }


    /**
     * 保存 cookies
     * */
    fun saveCookies(cookies: String) {
        if (cookies.isNotEmpty()) fastKV.putString(KEY_COOKIES, cookies)
    }

    /**
     * 获取 cookies
     * */
    fun getCookies(): String? {
        return fastKV.getString(KEY_COOKIES)
    }

    /**
     * 清除 cookies
     * */
    fun clearCookies() {
        fastKV.clear()
    }

    /**
     * 解析 cookies
     * */
    fun encodeCookie(cookies: List<String>?): String {
        val sb = StringBuilder()
        val set = HashSet<String>()
        cookies
            ?.map { cookie ->
                cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
            ?.forEach {
                it.filterNot { set.contains(it) }.forEach { set.add(it) }
            }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }


}