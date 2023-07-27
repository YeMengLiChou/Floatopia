package cn.csd.lib_framework.starter.task

import android.content.Context
import android.os.Process
import cn.csd.lib_framework.starter.dispatcher.DispatcherExecutor
import cn.csd.lib_framework.starter.dispatcher.TaskDispatcher
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 * 执行任务的基类
 * ```
 * class InitXxxTask(xxx) : Task() {
 *      //  异步线程执行的 Task 在被调用 await 的时候等待
 *      override fun needWait(): Boolean = true
 *
 *      override fun run() {
 *          // 需要执行的初始化逻辑
 *      }
 *
 *      // 依赖某些任务，在某些任务完成后才能执行
 *      override fun dependsOn(): MutableList<Class<out Task>> {
 *          // 把需要依赖的任务添加到列表
 *          return mutableListOf (
 *              InitXxxTask::class.java,
 *              ....
 *          )
 *      }
 *      // 指定执行的线程池，分为 CPU 和 IO, 默认为 IO
 *      override fun runOn(): ExecutorService? {
 *          return DispatcherExecutor.mIOExecutor
 *      }
 * }
 * ```
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
abstract class Task: ITask {

    protected var mContext: Context? = TaskDispatcher.mContext

    protected var mIsMainProcess: Boolean = TaskDispatcher.isMainProcess

    // 是否正在等待
    @Volatile
    var isWaiting = false

    // 是否正在执行
    @Volatile
    var isRunning = false

    // Task是否执行完成
    @Volatile
    var isFinished = false

    // Task是否已经被分发
    @Volatile
    var isDispatched = false


    // 当前Task依赖的Task数量（需要等待被依赖的Task执行完毕才能执行自己），默认没有依赖
    private val mDepends = CountDownLatch(
        dependOn()?.size ?: 0
    )

    /**
     * 等待前面依赖的 Task 执行完毕
     * */
    fun waitToSatisfy() {
        try {
            mDepends.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 前面依赖的 Task 执行玩一个
     * */
    fun satisfy() {
        mDepends.countDown()
    }
    /**
     * 是否需要尽快执行，解决特殊场景的问题：一个Task耗时非常多但是优先级却一般，很有可能开始的时间较晚，
     * 导致最后只是在等它，这种可以早开始。
     * */
    fun needRunAsSoon(): Boolean {
        return false
    }


    /**
     * Task的优先级，运行在主线程则不要去改优先级
     * */
    override fun priority(): Int {
        return Process.THREAD_PRIORITY_BACKGROUND
    }

    /**
     * 指定 Task 在哪个线程池执行，默认在 IO 线程池
     *
     * 如果是 CPU 密集型的需要指定为 [DispatcherExecutor.mCPUExecutor]
     *
     * */
    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.mIOExecutor
    }

    /**
     * 不在主线程运行
     * */
    override fun runOnMainThread(): Boolean = false

    override fun dependOn(): List<Class<out Task>>? {
        return null
    }

    /**
     * 异步线程执行的Task是否需要在被调用await的时候等待，默认不需要
     * */
    override fun needWait(): Boolean = false

    /**
     * 是否只在主进程，默认是
     * */
    override fun onlyInMainProcess(): Boolean = true

    override val tailRunnable: Runnable?
        get() = null

    override fun setTaskCallBack(callBack: TaskCallback) {

    }

    override fun needCall(): Boolean = false
}