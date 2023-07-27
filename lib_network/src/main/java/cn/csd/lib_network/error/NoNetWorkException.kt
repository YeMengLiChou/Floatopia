package cn.csd.lib_network.error

import java.io.IOException

/**
 * 无网络异常
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
class NoNetWorkException(
    val errorCode: Int,
    val errorMsg: String,
    val error: Throwable? = null
) : IOException(error) {

    constructor(error: ERROR, e: Throwable? = null) : this(error.code, error.errMsg, e)
}
