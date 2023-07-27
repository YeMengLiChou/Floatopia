package cn.csd.lib_framework.decoration

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * [LDividerItemDecoration] 是 [RecyclerView.ItemDecoration] 的 子类，可以用当作子项之间的分割线
 * 实际上是 [androidx.recyclerview.widget.DividerItemDecoration] 的一些改进
 * - 能够绘制最后一个子项的分割线
 * ```
 * val decoration = DividerItemDecoration(context, layoutManager.orientation)
 * recyclerView.addItemDecoration(decoration)
 *```
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */

class LDividerItemDecoration(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {

    companion object {
        private val TAG = LDividerItemDecoration::class.java.simpleName

        const val HORIZONTAL = LinearLayout.HORIZONTAL

        const val VERTICAL = LinearLayout.VERTICAL

        private val ATTRS = intArrayOf(R.attr.listDivider)
    }


    /**
     * 当前 divider 的 drawable
     */
    var drawable: Drawable?
        private set
    /**
     * 是否绘制最后一个子项的分割线
     * */
    private var mDrawLastPositionDivider = true

    /**
     * 当前方向:  [HORIZONTAL] or [VERTICAL].
     */
    private var mOrientation = 0

    private val mBounds = Rect()


    /**
     * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
     * [LinearLayoutManager].
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be [.HORIZONTAL] or [.VERTICAL].
     */
    init {
        val attr = context.obtainStyledAttributes(ATTRS)
        drawable = attr.getDrawable(0)
        if (drawable == null) {
            Log.w(
                TAG, "@android:attr/listDivider was not set in the theme used for this "
                        + "DividerItemDecoration. Please set that attribute all call setDrawable()"
            )
        }
        attr.recycle()
        setOrientation(orientation)
    }


    /**
     * 设置方向，如果 [RecyclerView.LayoutManager] 改变方向，需要调用该方法
     *
     * @param orientation [HORIZONTAL] or [VERTICAL]
     */
    fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL && orientation != VERTICAL)) {
            "Invalid orientation. It should be either HORIZONTAL or VERTICAL"
        }
        mOrientation = orientation
    }

    /**
     * 设置分割线的 [Drawable].
     *
     * @param drawable
     */
    fun setDrawable(drawable: Drawable) {
        this.drawable = drawable
    }

    fun isDrawLastPositionDivider(isDraw: Boolean) {
        mDrawLastPositionDivider = isDraw
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || drawable == null) {
            return
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent, state)
        } else {
            drawHorizontal(c, parent, state)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val left: Int
        val right: Int
        // 有设置 padding, 分割线需要要设置对应的 'padding'
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            // 将 绘制区域 限制在 Padding 区域
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        val lastPosition = state.itemCount - 1
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childRealPosition = parent.getChildAdapterPosition(child)
            if (mDrawLastPositionDivider || childRealPosition < lastPosition) {
                // 测量出子项包括了 margin 和 item decoration 的范围
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                // 确定 drawable 的绘制范围
                val bottom = mBounds.bottom + child.translationY.roundToInt()
                val top = bottom - drawable!!.intrinsicHeight
                // top 和 bottom 之间就只有 drawable 的高度
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        val childCount = parent.childCount
        val lastPosition = state.itemCount - 1
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childRealPosition = parent.getChildAdapterPosition(child)
            if (mDrawLastPositionDivider || childRealPosition < lastPosition) {
                // 测量出包括了 margin 和 item decoration 的范围
                parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
                // 确定 drawable的范围
                val right = mBounds.right + child.translationX.roundToInt()
                val left = right - drawable!!.intrinsicWidth
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (drawable == null) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, drawable!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, drawable!!.intrinsicWidth, 0)
        }
    }




}