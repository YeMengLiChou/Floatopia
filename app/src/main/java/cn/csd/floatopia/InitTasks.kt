package cn.csd.floatopia

import android.app.Application
import cn.csd.lib_framework.helper.AppHelper
import cn.csd.lib_framework.manager.AppManager
import cn.csd.lib_framework.util.LogUtils
import cn.csd.lib_framework.starter.task.Task
import cn.csd.lib_framework.toast.ToastUtils

/**
 * Application 启动时需要初始化的组件
 *
 *  看看 [Task] 和 [ TaskDispatcher]
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */


/**
 * 初始化 [AppHelper]
 * */
class InitAppHelperTask (
    private val application: Application,
    private val isDebug: Boolean
) : Task() {

    override fun needWait(): Boolean = true

    override fun run() {
        AppHelper.init(application, isDebug)
    }

    override val tailRunnable: Runnable?
        get() = Runnable {
            LogUtils.d("${this::class.simpleName} init successfully")
        }
}
/**
 * 初始化 [AppManager]
 * */
class InitAppManagerTask(
    private val application: Application
) : Task() {
    override fun run() {
        AppManager.init(application)
    }
    override val tailRunnable: Runnable?
        get() = Runnable {
            LogUtils.d("${this::class.simpleName} init successfully")
        }
}

/**
 * 初始化 []
 *
 * */
class InitToastUtilsTask(
    private val application: Application
): Task() {
    override fun run() {
        ToastUtils.init(application)
    }
    override val tailRunnable: Runnable?
        get() = Runnable {
            LogUtils.d("${this::class.simpleName} init successfully")
        }
}