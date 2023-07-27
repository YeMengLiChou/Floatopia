package cn.csd.lib_framework.starter.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import android.util.ArraySet
import cn.csd.lib_framework.starter.task.Task
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
object TaskUtils {

    // ======================== TaskProcess =====================================
    private var mCurProcessName: String? = null

    /** 通过读取系统文件来获取当前进程名 */
    private val curProcessNameFromProc: String?
        get() {
            try {
                // 读取 /proc/xxx/cmdline 文件中的命令行信息
                BufferedReader(
                    InputStreamReader(
                        FileInputStream("/proc/${Process.myPid()}/cmdline"), "iso-8859-1"
                    )
                ).use { reader ->
                    var char: Int
                    val processName = StringBuilder()
                    while (reader.read().also { char = it } > 0) {
                        processName.append(char.toChar())
                    }
                    return processName.toString()
                }
            } catch (e: Throwable) {  /* ignore */ }
            return null
        }

    /**
     * 通过 [context] 判断是否在主进程中运行
     * */
    fun isMainProcess(context: Context): Boolean {
        val processName = getCurProcessName(context)
        return if (processName != null && processName.contains(":")) {
            false
        } else {
            processName != null && processName == context.packageName
        }
    }

    /**
     * 根据 [ActivityManager] 判断当前的进程名
     * */
    fun getCurProcessName(context: Context): String? {
        val procName = mCurProcessName
        if (!TextUtils.isEmpty(procName)) {
            return procName
        }
        try {
            val pid = Process.myPid()
            val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    mCurProcessName = appProcess.processName
                    return mCurProcessName
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mCurProcessName = curProcessNameFromProc
        return mCurProcessName
    }

    // ======================= TaskSort  ===================================
    /** 高优先级的 Task */
    private val mNewHighTask = mutableListOf<Task>()

    /**
     * 任务的有向无环图的拓扑排序
     * */
    @Synchronized
    fun sortTasks(
        originTasks: List<Task>,
        clsLaunchTasks: List<Class<out Task>>
    ): List<Task> {
        val makTime = System.currentTimeMillis()
        val dependSet: MutableSet<Int> = ArraySet()
        val graph = DirectionGraph(originTasks.size)

        for (i in originTasks.indices) {
            val task = originTasks[i]
            // 如果该任务已经被分发或者没有依赖，则无需加入图
            if (task.isDispatched || task.dependOn().isNullOrEmpty()) continue

            task.dependOn()?.forEach {
                val indexOfDepend = getIndexOfTask(originTasks, clsLaunchTasks, it)
                check(indexOfDepend >= 0) {
                    task.javaClass.simpleName +
                            " depends on " + it.simpleName + " can not be found in task list "
                }
                dependSet.add(indexOfDepend)
                graph.addEdge(indexOfDepend, i)
            }
        }
        // 拓扑排序后的顺序
        val indexList: List<Int> = graph.topologicalSort()
        // 最终的任务顺序
        return getResultTasks(originTasks, dependSet, indexList)
    }

    /**
     * 查找在任务列表中的位置
     * */
    private fun getIndexOfTask(
        originTasks: List<Task>,
        clsLaunchTasks: List<Class<out Task>>,
        cls: Class<*>
    ): Int {
        val index = clsLaunchTasks.indexOf(cls)
        if (index >= 0) return index

        val size = originTasks.size
        for (i in 0 until size) {
            if (cls.simpleName == originTasks[i].javaClass.simpleName) {
                return i
            }
        }
        return index
    }

    private fun getResultTasks(
        originTasks: List<Task>,
        dependSet: Set<Int>,
        indexList: List<Int>
    ): List<Task> {
        val allTasks: MutableList<Task> = ArrayList(originTasks.size)
        // 被其他依赖的 Task
        val dependedTasks: MutableList<Task> = ArrayList()
        // 没有被依赖的 Task
        val independentTasks: MutableList<Task> = ArrayList()
        // 需要提升自己的优先级
        val runAsSoonTask: MutableList<Task> = ArrayList()

        for (index in indexList) {
            if (dependSet.contains(index)) {
                dependedTasks.add(originTasks[index])
            } else {
                val task = originTasks[index]
                if (task.needRunAsSoon()) {
                    runAsSoonTask.add(task)
                } else {
                    independentTasks.add(task)
                }
            }
        }
        // 被其他依赖 --> 需提升自身优先级 --> 需要等待前面 --> 没有依赖的
        mNewHighTask.apply {
            addAll(dependedTasks)
            addAll(runAsSoonTask)
        }
        allTasks.apply {
            addAll(mNewHighTask)
            addAll(independentTasks)
        }
        return allTasks
    }





    /**
     * 有向无环图
     * */
    private class DirectionGraph (
        private val mVertexCount: Int // 顶点数
    ) {
        /** 邻接表 */
        private val mAdjTable: Array<MutableList<Int>> = Array(mVertexCount) { mutableListOf() }

        /**
         * 添加边 <u, v>
         * @param u
         * @param v
         * */
        fun addEdge(u: Int, v: Int) {
            mAdjTable[u].add(v)
        }

        /**
         * 拓扑排序
         * */
        fun topologicalSort(): LinkedList<Int> {
            // 入度
            val inDegree = IntArray(mVertexCount)
            // 初始化入度
            for (i in 0 until mVertexCount) {
                mAdjTable[i].forEach {
                    inDegree[it] ++
                }
            }

            val queue: Queue<Int> = LinkedList()
            // 先找所有入度为 0 的点
            for (i in 0 until  mVertexCount) {
                if (inDegree[i] == 0) queue.add(i)
            }
            var cnt = 0
            val topOrder = LinkedList<Int>()
            while (queue.isNotEmpty()) {
                val u = queue.poll()
                u?.let {
                    topOrder.add(u)
                    // u 邻接点的入度-1，同时把入度为0的入队
                    mAdjTable[u].forEach {
                        if (-- inDegree[it] == 0) queue.add(it)
                    }
                }
                cnt ++
            }
            // 检查是否存在环
            check (cnt == mVertexCount) {
                "Exists a cycle in the graph"
            }
            return topOrder
        }
    }
}