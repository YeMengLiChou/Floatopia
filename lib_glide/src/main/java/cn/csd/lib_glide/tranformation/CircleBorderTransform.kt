package cn.csd.lib_glide.tranformation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CircleCrop

/**
 * 圆形边框的 Glide Transformation
 * @param borderColor 边框颜色
 * @param borderWidth 边框宽度 (px)
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/15
 */
class CircleBorderTransform(
    private val borderWidth: Float,
    private val borderColor: Int
) : CircleCrop() {
    // 抗锯齿的 Paint
    private var borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        // 调用父类的 方法处理 bitmap 为圆形
        val transform = super.transform(pool, toTransform, outWidth, outHeight)
        val canvas = Canvas(transform)
        // 中心点
        val cx = (outWidth / 2).toFloat()
        val cy = (outHeight / 2).toFloat()
        // 画圆
        canvas.drawCircle(
            cx,
            cy,
            cx.coerceAtMost(cy) - borderWidth / 2,
            borderPaint
        )
        canvas.setBitmap(null)
        return transform
    }
}
