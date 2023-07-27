package cn.csd.lib_glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import cn.csd.lib_framework.manager.ActivityManager
import cn.csd.lib_glide.tranformation.BlurTransformation
import cn.csd.lib_glide.tranformation.CircleBorderTransform
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.io.File

/**
 * Glide 的扩展类
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */

/**
 * 加载 [url] 图片
 * @param url 图片地址
 * @param memoryCache 是否开启缓存，默认为开启[false]
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setUrl(
    url: String,
    memoryCache: Boolean = false,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null
) {
    if (ActivityManager.isActivityDestroy(context)) return

    Glide.with(context).load(url)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke()
                return false
            }
        }) // 回调
        .skipMemoryCache(!memoryCache) // 启用内存缓存
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        .into(this)
}

/**
 * 加载 [url] **圆形** 图片
 * @param url 图片地址
 * @param memoryCache 是否开启缓存，默认为开启[false]
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setUrlCircle(
    url: String,
    memoryCache: Boolean = false,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (Drawable?.() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null,
) {
    if (ActivityManager.isActivityDestroy(context)) return

    // 圆形裁剪
    val options = RequestOptions.circleCropTransform()
    Glide.with(context).load(url)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke(resource)
                return false
            }
        }) // 回调
        .skipMemoryCache(false) // 启用内存缓存
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        .apply(options) // 圆形
        .into(this)
}

/**
 * 加载 [url] **圆形边框** 图片
 * @param url 图片地址
 * @param borderWidth 边框宽度
 * @param borderColor 边框颜色
 * @param memoryCache 是否开启缓存，默认为开启[false]
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setUrlCircleBorder(
    url: String,
    borderWidth: Float,
    borderColor: Int,
    memoryCache: Boolean = false,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (Drawable?.() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null,
) {
    if (ActivityManager.isActivityDestroy(context)) return

    Glide.with(context).load(url)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke(resource)
                return false
            }
        }) // 回调
        .skipMemoryCache(!memoryCache) // 启用内存缓存
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        .transform(CircleBorderTransform(borderWidth, borderColor)) // 圆形边框转换
        .into(this)
}


/**
 * 加载 [url] **圆角** 图片
 * @param url 图片地址
 * @param radius 圆角大小(dp)
 * @param memoryCache 是否开启缓存，默认为开启[false]
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setUrlRound(
    url: String,
    radius: Int,
    memoryCache: Boolean = false,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (Drawable?.() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null,
) {
    if (ActivityManager.isActivityDestroy(context)) return

    Glide.with(context).load(url)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke(resource)
                return false
            }
        }) // 回调
        .skipMemoryCache(!memoryCache) // 启用内存缓存
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        .transform(CenterCrop(), RoundedCorners(radius)) // 圆角转换
        .into(this)
}

/**
 * 加载 [url] **高斯模糊** 图片
 * @param url 图片地址
 * @param blur 模糊度[[0, 25]], 默认为 25
 * @param sampling 缩放比例，默认为 1
 * @param memoryCache 是否开启缓存，默认为 开启 [false]
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setUrlBlur(
    url: String,
    blur: Int= 25,
    sampling: Int = 1,
    memoryCache: Boolean = false,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (Drawable?.() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null,
) {
    if (ActivityManager.isActivityDestroy(context)) return
    // 高斯模糊配置
    val options = RequestOptions.bitmapTransform(BlurTransformation(blur, sampling))
    Glide.with(context).load(url)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke(resource)
                return false
            }
        }) // 回调
        .skipMemoryCache(!memoryCache) // 启用内存缓存
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        .apply(options) // 圆角转换
        .into(this)
}

/**
 * 加载 [url] **GIF** 图片
 * @param url 图片地址
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setUrlGIF(
    url: String,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (GifDrawable?.() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null,
) {
    if (ActivityManager.isActivityDestroy(context)) return

    Glide.with(context).asGif().load(url)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<GifDrawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: Target<GifDrawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke(resource)
                return false
            }

        }) // 回调
        .skipMemoryCache(true) // 不内存缓存
        .diskCacheStrategy(DiskCacheStrategy.DATA) // 磁盘缓存策略
        .into(this)
}

/**
 * 加载 [file] **GIF** 图片
 * @param file 图片文件
 * @param placeholder 自定义占位图片, 默认为 [R.mipmap.img_loading]
 * @param error 自定义加载失败的图片，默认为 [R.mipmap.img_load_error]
 * @param readyCallback 加载成功的回调
 * @param errorCallback 加载失败的回调
 * */
fun ImageView.setFile(
    file: File,
    @DrawableRes placeholder: Int = -1,
    @DrawableRes error: Int = -1,
    readyCallback: (Drawable?.() -> Unit)? = null,
    errorCallback: (() -> Unit)? = null,
) {
    if (ActivityManager.isActivityDestroy(context)) return

    Glide.with(context).load(file)
        .placeholder(if (placeholder == -1) R.mipmap.img_loading else placeholder) // 占位符，异常时显示的图片
        .error(if (error == -1) R.mipmap.img_load_error else error) // 错误时显示的图片
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                errorCallback?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                readyCallback?.invoke(resource)
                return false
            }

        }) // 回调
        .skipMemoryCache(false) // 内存缓存
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // 磁盘缓存策略
        .into(this)
}




