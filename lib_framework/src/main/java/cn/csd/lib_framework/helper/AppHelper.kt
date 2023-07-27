package cn.csd.lib_framework.helper

import android.app.Application

/**
 * 全局 Application 帮助类
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */
object AppHelper {

    private lateinit var application: Application
    private var isDebug = false


    fun init(application: Application, isDebug: Boolean) {
        this.application = application
        this.isDebug = isDebug
    }
    /**
     * application 实例
     * */
    fun getApplication() = application

    /**
     * 是否为 debug 环境
     */
    fun isDebug() = isDebug
}