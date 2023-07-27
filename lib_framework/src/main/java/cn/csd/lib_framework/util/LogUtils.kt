package cn.csd.lib_framework.util

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import cn.csd.lib_framework.helper.AppHelper
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.Flattener2
import com.elvishew.xlog.formatter.border.BorderFormatter
import com.elvishew.xlog.formatter.message.`object`.ObjectFormatter
import com.elvishew.xlog.formatter.stacktrace.StackTraceFormatter
import com.elvishew.xlog.internal.SystemCompat
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.therouter.app.flowtask.lifecycle.FlowTask
import java.text.SimpleDateFormat
import java.util.*

/**
 * XLog的简单封装
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
object LogUtils {

    private lateinit var mContext: Application

    private var initialized: Boolean = false

    fun init(context: Application, tag: String? = null, isDebug: Boolean) {
        mContext = context
        initialized = true
        val config = LogConfiguration.Builder()

        // 调试模式下输出所有相关信息
        if (isDebug) {
            config.logLevel(LogLevel.ALL)
                .enableBorder() // 边框
                .borderFormatter(LogBorderFormatter) // 边框格式
                .stackTraceFormatter(LogStackTraceFormatter) // 堆栈格式
                .addObjectFormatter(Any::class.java, AnyObjectFormatter)
                .enableThreadInfo() // 输出当前线程信息
                .enableStackTrace(2) // 堆栈信息追踪深度，0为无限制
            tag?.let { config.tag(it) } // 全局 Tag
             XLog.init(config.build())
        }
        else {
            config.logLevel(LogLevel.DEBUG)
                .enableThreadInfo() // 输出当前线程信息
                .enableStackTrace(0) // 堆栈信息追踪深度，0为无限制
                .build()
            tag?.let { config.tag(it) } // 全局 Tag

            val filePrinter = FilePrinter.Builder(mContext.getExternalFilesDir(null)?.path + "/log") // 指定日志文件的目录路径
                .fileNameGenerator(DateFileNameGenerator()) // 自定义文件名称 默认值: ChangelessFileNameGenerator(“日志”)
                .backupStrategy(FileSizeBackupStrategy2(3 * 1024 * 1024, 2)) // 单个日志文件的大小默认:FileSizeBackupStrategy(1024 * 1024)
                .cleanStrategy(FileLastModifiedCleanStrategy (30L * 24L * 60L * 60L * 1000L))  // 日志文件存活时间，单位毫秒
                .flattener(LogFlattener) // 自定义flattener，控制打印格式
                .build()

            XLog.init(config.build(), filePrinter)
        }
        d("LogUtils#init", LogUtils::class.simpleName)
    }

    fun v(msg: String, tag: String? = null) {
        tag?.let { XLog.tag(it).v(msg) }
            ?: XLog.v(msg)
    }

    fun v(msg: Any?, tag: String? = null) {
        tag?.let { XLog.tag(it).v(msg) }
            ?: XLog.v(msg)
    }

    fun v(format: String, tag: String?, vararg args: Any?) {
        tag?.let { XLog.tag(it).v(format, args) }
            ?: XLog.v(format, args)
    }

    fun v(msg: String, throwable: Throwable, tag: String? = null) {
        tag?.let { XLog.tag(it).v(msg, throwable) }
            ?: XLog.v(msg, throwable)
    }

    fun i(msg: String, tag: String? = null) {
        tag?.let { XLog.tag(it).i(msg) }
            ?: XLog.i(msg)
    }

    fun i(msg: Any?, tag: String? = null) {
        tag?.let { XLog.tag(it).i(msg) }
            ?: XLog.i(msg)
    }

    fun i(format: String, tag: String?, vararg args: Any?) {
        tag?.let { XLog.tag(it).i(format, args) }
            ?: XLog.i(format, args)
    }

    fun i(msg: String, throwable: Throwable, tag: String? = null) {
        tag?.let { XLog.tag(it).i(msg, throwable) }
            ?: XLog.i(msg, throwable)
    }

    fun d(msg: String, tag: String? = null) {
        tag?.let { XLog.tag(it).d(msg) }
            ?: XLog.d(msg)
    }

    fun d(msg: Any?, tag: String? = null) {
        tag?.let { XLog.tag(it).d(msg) }
            ?: XLog.d(msg)
    }

    fun d(format: String, tag: String?, vararg args: Any?) {
        tag?.let { XLog.tag(it).d(format, args) }
            ?: XLog.d(format, args)
    }

    fun d(msg: String, throwable: Throwable, tag: String? = null) {
        tag?.let { XLog.tag(it).d(msg, throwable) }
            ?: XLog.d(msg, throwable)
    }

    fun w(msg: String, tag: String? = null) {
        tag?.let { XLog.tag(it).w(msg) }
            ?: XLog.w(msg)
    }

    fun w(msg: Any?, tag: String? = null) {
        tag?.let { XLog.tag(it).w(msg) }
            ?: XLog.w(msg)
    }

    fun w(format: String, tag: String?, vararg args: Any?) {
        tag?.let { XLog.tag(it).w(format, args) }
            ?: XLog.w(format, args)
    }

    fun w(msg: String, throwable: Throwable, tag: String? = null) {
        tag?.let { XLog.tag(it).w(msg, throwable) }
            ?: XLog.w(msg, throwable)
    }

    fun e(msg: String, tag: String? = null) {
        tag?.let { XLog.tag(it).e(msg) }
            ?: XLog.e(msg)
    }

    fun e(msg: Any?, tag: String? = null) {
        tag?.let { XLog.tag(it).e(msg) }
            ?: XLog.e(msg)
    }

    fun e(format: String, tag: String? = null, vararg args: Any?) {
        tag?.let { XLog.tag(it).e(format, args) }
            ?: XLog.e(format, args)
    }

    fun e(msg: String, throwable: Throwable, tag: String? = null) {
        tag?.let { XLog.tag(it).e(msg, throwable) }
            ?: XLog.e(msg, throwable)
    }


    fun json(json: String, tag: String? = null) {
        tag?.let { XLog.tag(it).json(json)}
            ?: XLog.json(json)
    }

    fun xml(xml: String, tag: String? = null) {
        tag?.let { XLog.tag(it).json(xml)}
            ?: XLog.json(xml)
    }





    /**
     * 边框样式
     * */
    private object LogBorderFormatter : BorderFormatter {

        private const val VERTICAL_BORDER_CHAR = '║'

        // Length: 100.
        private const val TOP_HORIZONTAL_BORDER = "╔═════════════════════════════════════════════════" +
                "══════════════════════════════════════════════════"

        // Length: 99.
        private const val DIVIDER_HORIZONTAL_BORDER = "╟─────────────────────────────────────────────────" +
                "──────────────────────────────────────────────────"

        // Length: 100.
        private const val BOTTOM_HORIZONTAL_BORDER = "╚═════════════════════════════════════════════════" +
                "══════════════════════════════════════════════════"


        override fun format(segments: Array<out String>?): String {
            if (segments == null || segments.isEmpty()) {
                return ""
            }
            val nonNullSegments = arrayOfNulls<String>(segments.size)
            var nonNullCount = 0
            for (segment in segments) {
                nonNullSegments[nonNullCount++] = segment
            }
            if (nonNullCount == 0) {
                return ""
            }

            val msgBuilder = StringBuilder()
            msgBuilder.append("     ").append(SystemCompat.lineSeparator)
            msgBuilder.append(TOP_HORIZONTAL_BORDER).append(SystemCompat.lineSeparator)
            for (i in 0 until nonNullCount) {
                msgBuilder.append(nonNullSegments[i]?.let { appendVerticalBorder(it) })
                if (i != nonNullCount - 1) {
                    msgBuilder.append(SystemCompat.lineSeparator).append(DIVIDER_HORIZONTAL_BORDER)
                        .append(SystemCompat.lineSeparator)
                } else {
                    msgBuilder.append(SystemCompat.lineSeparator).append(BOTTOM_HORIZONTAL_BORDER)
                }
            }
            return msgBuilder.toString()
        }

        private fun appendVerticalBorder(msg: String): String {
            val borderedMsgBuilder = java.lang.StringBuilder(msg.length + 10)
            val lines = msg.split(SystemCompat.lineSeparator.toRegex()).toTypedArray()
            var i = 0
            val N = lines.size
            while (i < N) {
                if (i != 0) {
                    borderedMsgBuilder.append(SystemCompat.lineSeparator)
                }
                val line = lines[i]
                borderedMsgBuilder.append(VERTICAL_BORDER_CHAR).append(line)
                i++
            }
            return borderedMsgBuilder.toString()
        }
    }


    /**
     * 堆栈信息输出
     * */
    private object LogStackTraceFormatter : StackTraceFormatter {

        override fun format(stackTrace: Array<StackTraceElement>): String {
            val sb = java.lang.StringBuilder(256)
            return if (stackTrace.isEmpty()) {
                ""
            } else if (stackTrace.size == 1) {
                "\t─ " + stackTrace[0].toString()
            } else {
//                sb.append('\n')
                var i = 0
                val N = stackTrace.size
                while (i < N) {
                    if (i != N - 1) {
                        sb.append("\t├ ")
                        sb.append(stackTrace[i].toString())
                        sb.append(SystemCompat.lineSeparator)
                    } else {
                        sb.append("\t└ ")
                        sb.append(stackTrace[i].toString())
                    }
                    i++
                }
                sb.toString()
            }
        }
    }

    private object AnyObjectFormatter : ObjectFormatter<Any> {
        override fun format(data: Any): String {
            val sb = StringBuilder()
            sb.append(data::class.simpleName)
                .append("\t└ ")
                .append(data.toString())
            return sb.toString()
        }
    }

    private object LogFlattener  : Flattener2 {

        override fun flatten(timeMillis: Long, logLevel: Int, tag: String?, message: String?): CharSequence {
            return (getCurrDDate()
                    + '|' + LogLevel.getLevelName(logLevel)
                    + '|' + tag
                    + '|' + message)
        }

        @SuppressLint("ObsoleteSdkInt")
        fun getCurrDDate(): String? {
            return if (Build.VERSION.SDK_INT >= 24) {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(Date())
            } else {
                val tms = Calendar.getInstance()
                tms[Calendar.YEAR].toString() + "-" + tms[Calendar.MONTH] + "-" + tms[Calendar.DAY_OF_MONTH] + " " + tms[Calendar.HOUR_OF_DAY] + ":" + tms[Calendar.MINUTE] + ":" + tms[Calendar.SECOND] + "." + tms[Calendar.MILLISECOND]
            }
        }
    }


}