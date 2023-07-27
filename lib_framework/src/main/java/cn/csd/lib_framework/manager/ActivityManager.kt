package cn.csd.lib_framework.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import java.lang.StringBuilder

/**
 * Activity 管理类， Application 处有对 Activity 生命周期的监听
 * 手动管理
 * Created: 2023/07/15
 * @author Gleamrise
 */
object ActivityManager {

    private val activities = mutableListOf<Activity>()

    val activityCount
        get() = activities.size

    /** 入栈 */
    fun push(activity: Activity) {
        activities.add(activity)
    }
    /** 移除 */
    fun pop(activity: Activity) {
        activities.remove(activity)
    }

    /** 当前 activity */
    fun top(): Activity? = if (activities.isEmpty()) null else activities.last()

    /**
     * 结束所有 Activity
     * @param callback 结束后的回调
     * */
    fun finishAllActivity(callback: (() -> Unit)? = null) {
        val it = activities.iterator()
        while (it.hasNext()) {
            val item = it.next()
            it.remove()
            item.finish()
        }
        callback?.invoke()
    }

    /**
     *  结束其他 Activity
     *  @param clazz 指定不结束的 Activity
     **/
    fun finishOtherActivity(clazz: Class<out Activity>) {
        val it = activities.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item::class.java != clazz) {
                it.remove()
                item.finish()
            }
        }
    }

    /**
     * 结束指定 Activity
     * @param clazz 指定结束的 Activity
     * */
    fun finishActivity(clazz: Class<out Activity>) {
        val it = activities.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item::class.java == clazz) {
                it.remove()
                item.finish()
                break
            }
        }
    }

    /**
     * 判断是否存在 Activity
     * */
    fun isActivityExists(clazz: Class<out Activity>): Boolean {
        for (activity in activities) {
            if (activity::class.java == clazz) {
                return true
            }
        }
        return false
    }

    /**
     * 判断 Activity 是否销毁
     * */
    @SuppressLint("ObsoleteSdkInt")
    fun isActivityDestroy(context: Context): Boolean {
        val activity = findActivity(context)
        return if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activity.isDestroyed || activity.isFinishing
            } else activity.isFinishing
        } else true
    }


    /**
     * 判断是不是 Activity
     * - 继承链: [Activity] --> [ContextWrapper] --> [Context]
     * */
    private fun findActivity(context: Context): Activity? {
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return findActivity(context.baseContext)
        }
        return null
    }

    fun dumpStack(): String {
        val sb = StringBuilder()
        if (activities.isEmpty()) sb.append("empty")
        else {
            for (i in activityCount - 1 downTo 1) {
                sb.append(" ├ ").append(activities[i]::class.simpleName)
                    .append(" ")
                    .append(activities[i])
                    .append("\n")
            }
            sb.append(" └ ").append(activities[0]::class.simpleName)
                .append(" ")
                .append(activities[0])
                .append("\n")
        }
        return sb.toString()
    }
}