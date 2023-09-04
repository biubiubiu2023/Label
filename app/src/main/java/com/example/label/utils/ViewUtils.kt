package com.example.label.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.RectF
import android.os.Build
import android.view.WindowManager
import android.widget.ImageView
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * 创建时间：2023/9/3
 * 编写人： 陈陈陈
 * 功能描述：
 */
object ViewUtils {
    /**
     * 获取bitmap 在ImageView控件的真正位置
     */
    fun getImageRealPosition(view: ImageView): RectF {
        val imageMatrix = view.imageMatrix
        val drawable = view.drawable
        val rectf = RectF()
        if (drawable != null) {
            rectf.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            imageMatrix.mapRect(rectf)
        }
        return rectf
    }

    fun clip(
        src: Bitmap,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src, x, y, width, height)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }

    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getScreenWidth(): Int {
        val wm = Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            ?: return -1
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }

    fun getScreenHeight(): Int {
        val wm = Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            ?: return -1
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.y
    }

    fun getClipCachePath():String{
        var sdpath = PathUtils.getExternalAppDataPath()
        sdpath += "/cache/"
        val file = File(sdpath)
        if(!file.exists()){
            File(sdpath).mkdirs()
        }
        return sdpath
    }
}