package com.andyyang.camera2

import android.content.Context
import android.widget.Toast

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(this, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}







