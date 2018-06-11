package com.andyyang.camera2

import android.R.attr.path
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream


/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

object ImageLoader {

    @Synchronized fun loadImage(context: Context, url: String, view: ImageView) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .crossFade()
                .into(view)
    }

    @Throws(Exception::class)
    fun saveImageToGallery(context: Context, bmp: Bitmap) {
        // 首先保存图片
        val appDir = File(Environment.getExternalStorageDirectory(), "message")
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        val fos = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        // 其次把文件插入到系统图库
        MediaStore.Images.Media.insertImage(context.contentResolver,
                file.absolutePath, fileName, null)
        // 最后通知图库更新
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)))
    }

    @Throws(Exception::class)
    fun saveImageToGallery(context: Context, file: File) {
        MediaStore.Images.Media.insertImage(context.contentResolver,
                file.absolutePath, file.name, null)
        // 最后通知图库更新
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)))
    }

}
