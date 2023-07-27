package cn.csd.lib_framework.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * Flow 扩展
 *
 *
 * @author Gleamrise
 * Created: 2023/07/16
 * Tested:  2023/07/27
 */


/**
 * 倒计时，范围：(total, 0]
 *
 *
 * @param total 倒计总时间(s)
 * @param scope 启动的作用域，如果是需要更新视图，需要在 Dispatchers.Main 汇总起送费
 * @param onTick 间隔的回调
 * @param onStart 倒计时开始的回调
 * @param onCompletion 倒计时结束的回调
 * @return 该协程的 Job 实例
 *
 * ```
 * binding.btCountDown.click {
 *     countDown(
 *         total = 10,
 *         this.lifecycleScope,
 *         onTick = {
 *             binding.tvShow.text = it.toString()
 *         },
 *         onStart = {
 *             binding.tvShow.text = "start"
 *         },
 *         onCompletion = {
 *             binding.tvShow.text = "over"
 *         }
 *     )
 * }
 * ```
 *
 * */
fun countDown (
    total: Int,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    onTick: (Int) -> Unit,
    onStart: (() -> Unit)? = null,
    onCompletion: (() -> Unit)? = null
): Job {
    return flow {
        for (i in total - 1 downTo 0) {
            emit(i)
            delay(1000)
        }
    }
        .flowOn(Dispatchers.Default) // 指定在 Default 线程池执行flow
        .onStart { onStart?.invoke() }
        .onCompletion { onCompletion?.invoke() }
        .onEach { onTick.invoke(it) }
        .launchIn(scope) // 在 scope 启动收集，也是回调执行的线程
}
