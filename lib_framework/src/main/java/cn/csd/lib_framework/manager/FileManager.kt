package cn.csd.lib_framework.manager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.core.net.toUri
import cn.csd.lib_framework.helper.AppHelper
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *
 *
 * Created: 2023/07/15
 * @author Gleamrise
 */
object FileManager {
    const val TAG = "FileManager"

    // 媒体模块根目录
    private val DIR_SAVE_MEDIA_ROOT = Environment.DIRECTORY_DCIM

    // 媒体模块存储路径
    private val DIR_SAVE_MEDIA: String = "$DIR_SAVE_MEDIA_ROOT/Floatopia"

    //头像保存路径
    private const val DIR_AVATAR = "/avatar"

    // 图片缓存等保存路径
    private const val DIR_IMAGE = "FloatopiaImage"

    // JPG后缀
    const val SUFFIX_JPG = ".jpg"

    // PNG后缀
    const val SUFFIX_PNG = ".png"

    // MP4后缀
    const val SUFFIX_MP4 = ".mp4"

    // YUV后缀
    const val SUFFIX_YUV = ".yuv"

    // h264后缀
    const val SUFFIX_H264 = ".h264"

    /**
     * 获取jpg图片输出路径
     *
     * @return 输出路径
     */
    fun getJpgFileUri(): Uri? {
        // 文件名字
        val fileName = "${System.currentTimeMillis() / 1000}$SUFFIX_JPG"
        val headPath: String = getAvatarPath(fileName)
        val imgFile: File
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            imgFile = File(headPath)
            // 通过 MediaStore API 插入file 为了拿到系统裁剪要保存到的uri
            // App没有权限不能访问公共存储空间，需要通过 MediaStore API来操作
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, imgFile.absolutePath)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            return AppHelper.getApplication().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        } else {
            imgFile = File(headPath)
        }
        return imgFile.toUri()
    }

    /**
     * 通过媒体文件Uri获取文件-Android 11兼容
     *
     * @param fileUri 文件Uri
     */
    fun getMediaUri2File(fileUri: Uri): File? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = AppHelper.getApplication().contentResolver.query(
            fileUri, projection,
            null, null, null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val path = cursor.getString(columnIndex)
                cursor.close()
                return File(path)
            }
        }
        return null
    }

    /**
     * 头像地址
     */
    fun getAvatarPath(fileName: String): String {
        val path: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getFolderDirPath(DIR_SAVE_MEDIA + DIR_AVATAR)
        } else {
            getSaveDir(DIR_AVATAR)
        }
        return path + File.separator + fileName
    }



    fun getFolderDirPath(dstDirPathToCreate: String): String? {
        val dstFileDir = File(Environment.getExternalStorageDirectory(), dstDirPathToCreate)
        if (!dstFileDir.exists() && !dstFileDir.mkdirs()) {
            return null
        }
        return dstFileDir.absolutePath
    }

    /**
     * 获取具体模块存储目录
     */
    fun getSaveDir(directory: String): String {
        var path = ""
        path = if (TextUtils.isEmpty(directory) || "/" == directory) {
            ""
        } else if (directory.startsWith("/")) {
            directory
        } else {
            "/$directory"
        }
        path = getAppRootDir() + path
        val file = if (isSpace(path)) null else File(path)
        createOrExistsDir(file)
        return path
    }

    /**
     * 获取App存储根目录
     */
    fun getAppRootDir(): String {
        val path: String = getStorageRootDir()
        val file = if (isSpace(path)) null else File(path)
        Log.i(TAG, "getAppRootDir:$path")
        createOrExistsDir(file)
        return path
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsDir(file: File?): Boolean {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * 获取文件存储根目录
     */
    fun getStorageRootDir(): String {
        val filePath: File? = AppHelper.getApplication().getExternalFilesDir("")
        val path: String = if (filePath != null) {
            filePath.absolutePath
        } else {
            AppHelper.getApplication().filesDir.absolutePath
        }
        Log.i(TAG, "getStorageRootDir:$path")
        return path
    }

    /**
     * 创建图片换成目录
     */
    fun getImageDirectory(context: Context): File {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            //拿到Sd卡路径
            var directory = Environment.getExternalStorageDirectory()
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            }
            //创建一个文件夹
            val imageCache = File(directory, DIR_IMAGE)
            if (!imageCache.exists()) {
                imageCache.mkdirs()
            }

            return if (imageCache != null && imageCache.exists())
                imageCache else context.filesDir
        }
        return context.filesDir
    }

    // 获取文件大小
    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
    // 目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    fun getFileSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            var size2 = 0
            if (fileList != null) {
                size2 = fileList.size
                for (i in 0 until size2) {
                    // 如果下面还有文件
                    size = if (fileList[i].isDirectory) {
                        size + getFileSize(fileList[i])
                    } else {
                        size + fileList[i].length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 获取应用总缓存大小
     *
     * @param context
     * @param dirPaths 额外的缓存目录
     * @return
     */
    fun getTotalCacheSize(context: Context, vararg dirPaths: String?): String {
        return try {
            var cacheSize: Long = 0 // = FileUtils.getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED
            ) {
                val file = context.externalCacheDir
                cacheSize += if (file == null) 0 else getFileSize(file)
                for (dirPath in dirPaths) {
                    dirPath?.let {
                        val current = File(dirPath)
                        cacheSize += getFileSize(current)
                    }
                }
            }
            getFormatSize(cacheSize.toDouble())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            "0KB"
        }
    }

    /**
     * 格式化单位，保留两位小数的最大单位
     *
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return if (kiloByte == 0.0) {
                "0KB"
            } else size.toString() + "KB"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, RoundingMode.HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, RoundingMode.HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, RoundingMode.HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, RoundingMode.HALF_UP).toPlainString() + "TB")
    }

    /**
     * 递归删除目录下的所有文件
     * @param path 文件夹目录
     * @return 是否删除
     */
    fun delAllFile(path: String): Boolean {
        val file = File(path)
        // 文件夹不存在
        if (!file.exists()) {
            return false
        }
        // file不是文件夹
        if (!file.isDirectory) {
            return false
        }
        // 文件夹下面的文件
        val tempList = file.list() ?: return false
        // 成功删除的标志
        var flag = false
        var temp: File?
        for (i in tempList.indices) {
            temp =
                if (path.endsWith(File.separator)) {
                    File(path + tempList[i])
                } else {
                    File(path + File.separator + tempList[i])
                }
            // 文件删除
            if (temp.isFile) {
                temp.delete()
            }
            if (temp.isDirectory) {
                // 先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i])
                // 再删除空文件夹
                delFolder(path + "/" + tempList[i])
                flag = true
            }
        }
        return flag
    }

    /**
     * 删除空文件夹
     * @param path 文件夹地址
     * @return 是否删除成功，true为删除成功
     */
    private fun delFolder(path: String): Boolean {
        return try {
            val file = File(path)
            file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            false
        }
    }

    /**
     * 获取缓存目录
     */
    fun getDiskCacheDir(context: Context): String? {
        val cachePath: String? =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                context.externalCacheDir?.path
            } else {
                context.cacheDir.path
            }
        return cachePath
    }
}