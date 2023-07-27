package cn.csd.lib_framework.starter.task

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
object TaskStatistics {

    @Volatile
    private var mCurrentSituation = ""

    private var mBeans = mutableListOf<TaskStatisticsBean>()

    private val mTaskDoneCount = AtomicInteger()

    var currentSituation: String
        get() = mCurrentSituation
        set(value) {
            mCurrentSituation = value
            initStatistics()
        }

    fun markTaskDone() {
        mTaskDoneCount.getAndIncrement()
    }

    fun initStatistics() {
        val bean = TaskStatisticsBean(mCurrentSituation, mTaskDoneCount.get())
        mBeans.add(bean)
        mTaskDoneCount.set(0)
    }

}