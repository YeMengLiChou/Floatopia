package cn.csd.lib_network.response

/**
 * 网络请求基类
 * @param data 请求返回内容
 * @param code 服务器状态码，根据实际需求进行设置
 * @param msg 返回信息
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */

abstract class BaseResponse<out T>(
    val data: T?,
    val code: Int,
    val msg: String
) {

    /**
     * 根据 [code] 判断是否请求失败
     * */
    abstract fun isFailed(): Boolean

}