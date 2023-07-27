package cn.csd.lib_network.response


/**
 *
 *
 * Created: 2023/07/17
 * @author Gleamrise
 */
sealed class RequestResult <out T> {

    /** 请求成功时 */
    data class Success<out R>(val value: R) : RequestResult<R>()

    /** 请求发生异常时 */
    data class Error(val throwable: Throwable) : RequestResult<Nothing>()

    /** 请求重试时 */
    data class Retry(val attempt: Int): RequestResult<Int>()

    /** 请求进行时 */
    object Loading: RequestResult<Nothing>()

    /**
     * 请求 **成功** 时的回调
     * @param event 对该请求结果的处理
     * @return 是否处理该结果，true为处理
     * ```
     * result.onSuccess { value ->
     *     // handle with the value
     * }
     * ```
     * */
    inline fun <reified R> onSuccess(event: (value: T) -> Unit): Boolean {
        if (this is Success) {
            // 此时已经自动转为 Success 类型
            event.invoke(this.value)
            return true
        }
        return false
    }


    /**
     * 请求 **失败** 时的回调
     * @param event 对该请求结果的处理
     * @return 是否处理该结果，true为处理
     * ```
     * result.onError { throwable ->
     *     // handle with the throwable
     * }
     * ```
     * */
    inline fun onError(event: (e: Throwable) -> Unit): Boolean {
        if (this is Error) {
            // 此时已经自动转为 Error 类型
            event.invoke(this.throwable)
            return true
        }
        return false
    }

    /**
     * 请求 **重试** 时的回调
     * @param event 对该重试的处理
     * ```
     * result.onLoading {
     *     // handle with the loading
     * }
     * ```
     * */
    inline fun onRetry(event: (attempt: Int) -> Unit) {
        if (this is Retry) {
            // 此时已经自动转为 Retry 类型
            event.invoke(this.attempt)
        }
    }

    /**
     * 请求 **加载** 时的回调
     * @param event 对加载的处理
     * ```
     * result.onLoading {
     *     // handle with the loading
     * }
     * ```
     * */
    inline fun onLoading(event: () -> Unit) {
        if (this is Loading) {
            // 此时已经自动转为 Loading 类型
            event.invoke()
        }
    }

}




