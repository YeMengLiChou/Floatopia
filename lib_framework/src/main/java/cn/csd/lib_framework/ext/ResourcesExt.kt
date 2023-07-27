package cn.csd.lib_framework.ext

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import cn.csd.lib_framework.helper.AppHelper
import cn.csd.lib_framework.manager.AppManager

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
private val res: Resources
    get() = context.resources

private val context: Context
    get() = AppHelper.getApplication()

fun Context.color(id: Int) = ContextCompat.getColor(this, id)

fun Context.string(id: Int) = resources.getString(id)

fun Context.stringArray(id: Int) = resources.getStringArray(id)

fun Context.drawable(id: Int) = ContextCompat.getDrawable(this, id)

fun Context.dimenPx(id: Int) = resources.getDimensionPixelSize(id)

fun Context.integer(id: Int) = resources.getInteger(id)

inline val Context.layoutInflater: android.view.LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater

val screenWidth: Int get() = AppManager.getScreenWidthPx()

val screenHeight: Int get() = AppManager.getScreenHeightPx()


fun inflateLayout(
    @LayoutRes layoutId: Int,
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): View {
    return context.layoutInflater.inflate(layoutId, parent, attachToParent)
}