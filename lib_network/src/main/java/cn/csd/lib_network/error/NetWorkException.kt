package cn.csd.lib_network.error

/**
 * 网络异常类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
open class NetWorkException(
    val errorCode: Int,
    val errorMsg: String,
    val error: Throwable? = null
) : Exception(error) {

    constructor(error: ERROR, e: Throwable? = null) : this(error.code, error.errMsg, e)

}