package cn.csd.lib_framework.starter.task

import android.os.Process
import android.os.Trace
import cn.csd.lib_framework.starter.dispatcher.TaskDispatcher

/**
 * 执行 [Task] 的 [Runnable]
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
class TaskRunnable : Runnable {
    /** 需要执行的任务 */
    private var mTask: Task

    /** 调度器 */
    private var mTaskDispatcher: TaskDispatcher? = null

    constructor(task: Task) {
        mTask = task
    }

    constructor(task: Task, dispatcher: TaskDispatcher) {
        mTask = task
        mTaskDispatcher = dispatcher
    }


    override fun run() {
        Trace.beginSection(mTask::class.simpleName.toString())
        // TODO: log situation

        Process.setThreadPriority(mTask.priority())
        var startTime = System.currentTimeMillis()

        // 等待前面的 Task 执行
        mTask.isWaiting = true
        mTask.waitToSatisfy()
        val waitTime = System.currentTimeMillis() - startTime

        // 开始执行
        startTime = System.currentTimeMillis()
        mTask.isRunning = true
        mTask.run()

        // 执行后续指定的 runnable
        mTask.tailRunnable?.run()

        if (!mTask.needCall() || !mTask.runOnMainThread()) {
            // TODO: log time

            TaskStatistics.markTaskDone()
            mTask.isFinished = true
            mTaskDispatcher?.apply {
                notifyNext(mTask)
                markTaskDone(mTask)
            }
            // TODO: log task finished
        }
        Trace.endSection()
    }
}