package cn.csd.lib_framework.starter.dispatcher

import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
object DispatcherExecutor {
    /** Java 虚拟机可用的处理器数 */
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()

    /** 线程池大小 [[CPU_COUNT - 1, 5]] */
    private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))

    private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

    /** 线程的空闲时间 */
    private const val KEEP_ALIVE_SECONDS = 5

    /** 处理队列 */
    private val poolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()

    /** 线程工厂 */
    private val mThreadFactory = DefaultThreadFactory()

    /** 处理没有被线程池执行的 Runnable */
    private val mHandler = RejectedExecutionHandler { r, _ ->
        //
        Executors.newCachedThreadPool().execute(r)
    }

    /** 获取 CPU 线程池 */
    var mCPUExecutor: ThreadPoolExecutor? = null
        private set

    /** 获取 IO 线程池 */
    var mIOExecutor: ExecutorService? = null
        private set

    init {
        mCPUExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_SECONDS.toLong(),
            TimeUnit.SECONDS,
            poolWorkQueue,
            mThreadFactory,
            mHandler
        ).apply { allowCoreThreadTimeOut(true) }
        mIOExecutor = Executors.newCachedThreadPool(mThreadFactory)
    }



    /**
     * 默认线程工厂
     * */
    private class DefaultThreadFactory : ThreadFactory {

        /** 管理相关的线程 */
        private val group: ThreadGroup?

        /** 线程池线程数量 */
        private val threadNumber = AtomicInteger(1)

        /** 线程名字前缀 */
        private val namePrefix: String

        companion object {
            /** 当前线程数 */
            private val poolNumber = AtomicInteger(1)
        }

        init {
            val manager = System.getSecurityManager()
            group = manager?.threadGroup ?: Thread.currentThread().threadGroup ?: null
            namePrefix = "TaskDispatcherPool-${poolNumber.getAndIncrement()}-Thread-"
        }

        override fun newThread(runnable: Runnable?): Thread =
            Thread (group, runnable, namePrefix + threadNumber.getAndIncrement(), 0)
                .apply {
                    if (isDaemon) isDaemon = false
                    if (priority != Thread.NORM_PRIORITY) priority = Thread.NORM_PRIORITY
                }

    }

}