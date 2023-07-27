package cn.csd.lib_glide.tranformation

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.util.Util
import java.security.MessageDigest

/**
 * Glide Transformation 用于 Bitmap 的基类
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */
abstract class BitmapTransformation : Transformation<Bitmap?> {

    override fun transform(
        context: Context,
        resource: Resource<Bitmap?>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap?> {
        if (!Util.isValidDimensions(outWidth, outHeight)) {
            throw IllegalArgumentException(
                "Cannot apply transformation on width: " + outWidth + " or height: " + outHeight
                        + " less than or equal to zero and not Target.SIZE_ORIGINAL"
            )
        }
        val bitmapPool = Glide.get(context).bitmapPool
        // 原始 bitmap
        val toTransform = resource.get()
        // 目标宽高
        val targetWidth = if (outWidth == Target.SIZE_ORIGINAL) toTransform.width else outWidth
        val targetHeight = if (outHeight == Target.SIZE_ORIGINAL) toTransform.height else outHeight
        // 需实现如何处理的方法 transform
        val transformed = transform(context, bitmapPool, toTransform, targetWidth, targetHeight)
        return if ((toTransform == transformed)) resource else
            BitmapResource.obtain(transformed, bitmapPool)!!
    }

    // bitmap的密度与原始的保持已知
    fun setCanvasBitmapDensity(toTransform: Bitmap, canvasBitmap: Bitmap) {
        canvasBitmap.density = toTransform.density
    }

    // bitmap 转换实现
    protected abstract fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap

    abstract override fun updateDiskCacheKey(messageDigest: MessageDigest)

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int
}