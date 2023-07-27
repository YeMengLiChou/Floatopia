package cn.csd.lib_framework.util

import cn.csd.lib_framework.helper.AppHelper

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
object SizeUtils {

    private val displayMetrics get() =  AppHelper.getApplication().resources.displayMetrics

    fun dp2px(dpValue: Float): Int {
        return (dpValue * displayMetrics.density + 0.5f).toInt()
    }

    fun px2dp(pxValue: Float): Int {
        return (pxValue / displayMetrics.density + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return (spValue * displayMetrics.scaledDensity + 0.5f).toInt()
    }

    fun px2sp(pxValue: Float): Int {
        return (pxValue / displayMetrics.scaledDensity + 0.5f).toInt()
    }
}


fun Number.dp(): Int = SizeUtils.dp2px(toFloat())

val Number.dp: Int
    get() = dp()

fun Number.sp(): Int = SizeUtils.sp2px(toFloat())

val Number.sp: Int
    get() = sp()

