package cn.csd.lib_network.repository

import android.util.Log
import cn.csd.lib_network.error.ExceptionHandler
import cn.csd.lib_network.error.NetWorkException
import cn.csd.lib_network.response.BaseResponse
import cn.csd.lib_network.response.RequestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.withTimeout

/**
 * 获取数据的 [Repository] 基类
 *
 * Created: 2023/07/17
 * @author Gleamrise
 */
abstract class BaseRepository {

    /**
     * 网络请求，已经指定在 IO 线程中执行
     * @param timeout 设置超时(ms)，默认为不超时, <= 0 不超时, > 0 指定超时时间
     * @param retry 重试次数，默认为不重试 0
     * @param request 请求调用
     * @return flow RequestResult
     * */
    fun <T> requestResponse(
        timeout: Long = -1L,
        retry: Int = 0,
        request: suspend () ->  BaseResponse<T>,
    ): Flow<RequestResult<T>> {
        var loading = false

        return flow {
            // 请求发送中可以加载弹窗
            if (!loading) { // 保证重试时仅发送一次 Loading
                loading = true
                emit(RequestResult.Loading)
            }
            val response =
                if (timeout > 0) {
                    withTimeout(timeout) { request() }
                } else {
                    request()
                }
            // 请求失败则抛出 NetWorkException
            if (response.isFailed()) {
                throw NetWorkException(response.code, response.msg)
            } else {
                // 请求成功则发出请求结果
                response.data?.let { emit(RequestResult.Success(it)) }
                    ?: throw NetWorkException(response.code, "返回数据为空")
            }
        }
            .flowOn(Dispatchers.IO) // 在 IO 线程执行
            .retryWhen { cause, attempt ->
                Log.d("BaseRepository#requestResponse-retryWhen", cause.message.toString())
                attempt < retry // 少于重试次数则重试
            }
            .catch { cause ->
                // 重试次数过后的异常会被此处捕获，处理
                Log.d("BaseRepository#requestResponse-catch", cause.message.toString())
                emit(RequestResult.Error(ExceptionHandler.handleException(cause)))
            }
    }
}