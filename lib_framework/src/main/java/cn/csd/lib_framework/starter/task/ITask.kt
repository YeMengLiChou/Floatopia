package cn.csd.lib_framework.starter.task

import android.os.Process
import androidx.annotation.IntRange
import java.util.concurrent.Executor

/**
 * 执行任务的接口
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
interface ITask {

    /** 任务的优先级 */
    @IntRange(from = Process.THREAD_PRIORITY_FOREGROUND.toLong(), to = Process.THREAD_PRIORITY_LOWEST.toLong())
    fun priority(): Int

    /** 任务执行体 */
    fun run()

    fun runOn(): Executor?

    /** 是否在在主线程执行 */
    fun runOnMainThread(): Boolean

    /** 依赖关系 */
    fun dependOn(): List<Class<out ITask>>?

    /** 异步执行时是否需要 await 等待完成 */
    fun needWait(): Boolean

    /** 是否只在主线程中完成 */
    fun onlyInMainProcess(): Boolean

    /** run 执行完后所需要执行的任务 */
    val tailRunnable: Runnable?

    /** 设置回调 */
    fun setTaskCallBack(callBack: TaskCallback)

    /** 是否需要回调 */
    fun needCall(): Boolean

}