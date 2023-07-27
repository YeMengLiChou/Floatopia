package cn.csd.floatopia

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.csd.lib_framework.helper.AppHelper
import cn.csd.lib_framework.manager.ActivityManager
import cn.csd.lib_framework.starter.dispatcher.TaskDispatcher
import cn.csd.lib_framework.util.LogUtils

/**
 *
 *
 * Created: 2023/07/13
 * @author Gleamrise
 */

class FloatopiaApplication: Application() {
    companion object {
        private val TAG = FloatopiaApplication::class.simpleName
    }

    private val isDebug = true

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycle()

        LogUtils.init(this, "Floatopia", isDebug)
        TaskDispatcher.init(this)

        // 添加任务
        TaskDispatcher.createInstance()
            .addTask(InitAppHelperTask(this, isDebug))
            .addTask(InitAppManagerTask(this))
            .addTask(InitToastUtilsTask(this))
            .start() // 执行任务
            .await() // 等待执行完毕

        if (AppHelper.isDebug()) {
            LogUtils.d("All tasks have been initialized")
        }
    }


    /** activity 的生命周期监听 */
    private fun registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) { }

            override fun onActivityStarted(activity: Activity) { }

            override fun onActivityDestroyed(activity: Activity) {
                ActivityManager.pop(activity)
                if (AppHelper.isDebug()) {
                    LogUtils.d("${activity::class.simpleName} destroyed \n" +
                            "Current Activity: ${ActivityManager.activityCount}\n" +
                            ActivityManager.dumpStack()
                    )
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) { }

            override fun onActivityStopped(activity: Activity) { }

            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                ActivityManager.push(activity)
                if (AppHelper.isDebug()) {
                    LogUtils.d("${activity::class.simpleName} created\n" +
                            "Current Activity: ${ActivityManager.activityCount}\n" +
                            ActivityManager.dumpStack()
                    )
                }
            }

            override fun onActivityResumed(activity: Activity) { }
        })
    }
}