package cn.csd.lib_framework.toast

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cn.csd.lib_framework.R
import cn.csd.lib_framework.databinding.FrameworkLayoutToastBinding
import cn.csd.lib_framework.util.LogUtils
import cn.csd.lib_framework.util.dp
import java.time.Duration

/**
 * Toast 自定义工具类，因为 [Toast.setView] 被废弃，导致自定义Toast无法在后台弹出,尽量在应用内部使用
 *
 * - 通过 [Location] 可以设置 Toast 的位置
 * - 三种带图标的方法 [success]，[warn] 和 [error]
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
object ToastUtils {
    /**
     * 中间位置
     * */
    val CENTER = Location(Gravity.CENTER, 0, 0)

    /**
     * 上方居中位置
     * */
    val TOP = Location(Gravity.TOP or Gravity.CENTER, 0, 32.dp)

    /**
     * 下方居中位置
     * */
    val BOTTOM = Location(Gravity.BOTTOM or Gravity.CENTER, 0, 32.dp)

    /**
     * 左方居中位置
     * */
    val LEFT = Location(Gravity.LEFT or Gravity.CENTER, 32.dp, 0)

    /**
     * 右方居中位置
     * */
    val RIGHT = Location(Gravity.RIGHT or Gravity.CENTER, 32.dp, 0)

    private var toast: Toast? = null

    private lateinit var mContext: Application

    private val mToastHandler = Looper.myLooper()
        ?.let { Handler(it) }
        ?: Handler(Looper.getMainLooper())

    private val binding by lazy {
        FrameworkLayoutToastBinding.inflate(LayoutInflater.from(mContext), null, false)
    }

    /**
     * 需要初始化
     * */
    fun init(context: Application) {
        mContext = context
    }

    fun make(@StringRes stringId: Int,
             longDuration: Boolean = false,
             @DrawableRes drawableId: Int = 0,
             location: Location = CENTER
             ) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            if (longDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG,
            drawableId,
            location
        )
    }

    fun make(msg: String,
             longDuration: Boolean = false,
             @DrawableRes drawableId: Int = 0,
             location: Location = CENTER
             ) {
        toastImpl(
            msg,
            if (longDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG,
            drawableId,
            location
        )
    }

    fun make(msg: CharSequence,
             longDuration: Boolean = false,
             @DrawableRes drawableId: Int = 0,
             location: Location = CENTER
             ) {
        toastImpl(
            msg,
            if (longDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG,
            drawableId,
            location
        )
    }

    fun success(msg: String) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_success
        )
    }

    fun success(@StringRes stringId: Int, location: Location = CENTER) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_success
        )
    }

    fun success(msg: CharSequence, location: Location = CENTER) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_success,
            location
        )
    }

    fun warn(msg: String, location: Location = CENTER) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_warning,
            location
        )
    }

    fun warn(@StringRes stringId: Int, location: Location = CENTER) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_warning,
            location
        )
    }

    fun warn(msg: CharSequence, location: Location = CENTER) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_warning,
            location
        )
    }

    fun error(msg: String, location: Location = CENTER) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_error,
            location
        )
    }

    fun error(@StringRes stringId: Int, location: Location = CENTER) {
        val msg = mContext.getString(stringId)
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_error,
            location
        )
    }

    fun error(msg: CharSequence, location: Location = CENTER) {
        toastImpl(
            msg,
            Toast.LENGTH_SHORT,
            R.mipmap.widget_toast_error,
            location
        )
    }


    private fun cancel() {
        toast?.cancel()
        mToastHandler.removeCallbacksAndMessages(null)
    }

    private fun toastImpl(
        msg: CharSequence,
        duration: Int,
        @DrawableRes drawableId: Int = 0,
        location: Location = CENTER
    ) {
        toast?.let {
            cancel()
            toast = null
        }
        mToastHandler.postDelayed({
             try {
                 binding.frameworkTvToastContent.apply {
                     text = msg
                     setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
                 }
                 toast = Toast(mContext).apply {
                     view = binding.root
                     location.let {
                         setGravity(it.gravity, it.xOffset, it.yOffset)
                     }
                     this.duration = duration
                     show()
                 }
             } catch (e: Exception) {
                 e.printStackTrace()
             }
        }, 50)
    }

     data class Location(
        val gravity: Int,
        val xOffset: Int,
        val yOffset: Int
    )

}