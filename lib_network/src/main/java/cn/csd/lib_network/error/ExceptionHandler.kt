package cn.csd.lib_network.error

import android.net.ParseException
import android.util.MalformedJsonException
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * 统一处理异常
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
object ExceptionHandler {

    fun handleException(e: Throwable): NetWorkException {
        val networkException: NetWorkException = when (e) {
            is NetWorkException -> {
                // 登录失效
//                if (ex.errorCode == ERROR.UNLOGIN.code){
//
//                }
                NetWorkException(e.errorCode, e.errorMsg, e)
            }
            is NoNetWorkException -> {
//                TipsToast.showTips("网络异常，请尝试刷新")
                NetWorkException(ERROR.NETWORK_ERROR)
            }
            is HttpException -> {
                when (e.code()) {
                    ERROR.UNAUTHORIZED.code -> NetWorkException(ERROR.UNAUTHORIZED, e)
                    ERROR.FORBIDDEN.code -> NetWorkException(ERROR.FORBIDDEN, e)
                    ERROR.NOT_FOUND.code -> NetWorkException(ERROR.NOT_FOUND, e)
                    ERROR.REQUEST_TIMEOUT.code -> NetWorkException(ERROR.REQUEST_TIMEOUT, e)
                    ERROR.GATEWAY_TIMEOUT.code -> NetWorkException(ERROR.GATEWAY_TIMEOUT, e)
                    ERROR.INTERNAL_SERVER_ERROR.code -> NetWorkException(ERROR.INTERNAL_SERVER_ERROR, e)
                    ERROR.BAD_GATEWAY.code -> NetWorkException(ERROR.BAD_GATEWAY, e)
                    ERROR.SERVICE_UNAVAILABLE.code -> NetWorkException(ERROR.SERVICE_UNAVAILABLE, e)
                    else -> NetWorkException(e.code(), e.message(), e)
                }
            }
            is JsonParseException,
            is JSONException,
            is ParseException,
            is MalformedJsonException -> NetWorkException(ERROR.PARSE_ERROR, e)
            is ConnectException -> NetWorkException(ERROR.NETWORK_ERROR, e)
            is javax.net.ssl.SSLException -> NetWorkException(ERROR.SSL_ERROR, e)
            is java.net.SocketException -> NetWorkException(ERROR.TIMEOUT_ERROR, e)
            is java.net.SocketTimeoutException -> NetWorkException(ERROR.TIMEOUT_ERROR, e)
            is java.net.UnknownHostException -> NetWorkException(ERROR.UNKNOW_HOST, e)
            else -> {
                if (!e.message.isNullOrEmpty()) NetWorkException(1000, e.message!!, e)
                else NetWorkException(ERROR.UNKNOWN, e)
            }
        }
        return networkException

    }
}
