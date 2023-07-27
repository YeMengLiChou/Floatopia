package cn.csd.lib_network.utill

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import cn.csd.lib_framework.helper.AppHelper

/**
 *
 * 网络工具
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
object NetWorkUtils {
    /** 缓存回调 */
    private val cachedCallback = HashMap<String, NetworkCallback>()

    /**
     * 判断网络状态，有网络返回true
     */
    fun isConnected(context: Context = AppHelper.getApplication()): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)

        return hasCapability(caps) || hasTransports(caps)
    }

    /**
     * 注册网络状态监听,
     * 在 onResume 调用
     * @param context
     * @param key 用于取消注册的键值，需与 [unRegisterNetworkStatusListener] 中的 key 保持一致
     * @param callback 网络状态改变时回调
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun registerNetworkStatusListener(context: Context, key: String, callback: (network: Network, networkCapabilities: NetworkCapabilities) -> Unit) {
        val networkService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                callback(network, networkCapabilities)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkService.registerDefaultNetworkCallback(networkCallback)
        } else {
            networkService.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
        }
        cachedCallback[key] = networkCallback
    }

    /**
     * 取消注册网络状态监听, 在 [onPause] 或 [onDestroy] 取消
     * @param context
     * @param key 用于取消注册的键值，与 [registerNetworkStatusListener] 中 key 相同
     * */
    private fun unRegisterNetworkStatusListener(context: Context, key: String) {
        val networkService = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cachedCallback[key]?.let { networkService.unregisterNetworkCallback(it) }
    }


    /**
     * - [NetworkCapabilities.NET_CAPABILITY_INTERNET] 表示网络连接是否具有互联网功能。
     * - [NetworkCapabilities.NET_CAPABILITY_VALIDATED] 表示网络连接已经通过验证，并且可以访问互联网。
     *
     * */
    private fun hasCapability(caps: NetworkCapabilities?): Boolean {
        return caps?.run {
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false
    }

    /**
     * - [NetworkCapabilities.TRANSPORT_WIFI] 网络连接的传输类型为 wifi
     * - [NetworkCapabilities.TRANSPORT_VPN]  网络连接的传输类型为 VPN，
     * - [NetworkCapabilities.TRANSPORT_CELLULAR] 传输类型为蜂窝网络
     * */
    private fun hasTransports(caps: NetworkCapabilities?): Boolean {
        return caps?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false
    }
}


