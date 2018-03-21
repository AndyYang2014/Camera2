package com.andyyang.camera2

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import java.io.File

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

fun Context.showToast(content: String) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}


fun ImageView.displayUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        setImageResource(R.mipmap.ic_launcher)
    } else {
        ImageLoader.loadImage(context, "file://" + url, this)
    }
}

fun File.saveImageToGallery(context: Context) {
    ImageLoader.saveImageToGallery(context, this)
}







