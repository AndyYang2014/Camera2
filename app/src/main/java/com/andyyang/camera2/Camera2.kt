package com.andyyang.camera2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.util.DisplayMetrics
import android.view.View
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

fun getMetrics(): DisplayMetrics {
    return App.context.resources.displayMetrics
}

/**
 * 获取屏幕宽高
 */
fun getScreenWidth(): Int {
    return getMetrics().widthPixels
}

fun getScreenHeight(): Int {
    return getMetrics().heightPixels
}

/**
 * dp px互转
 */

fun Float.dp2px(): Int {
    val scale = getMetrics().density
    return (this * scale + 0.5F).toInt()
}

fun Float.px2dp(): Int {
    val scale = getMetrics().density
    return (this / scale + 0.5F).toInt()
}

fun Int.dp2px(): Int {
    return this.toFloat().dp2px()
}

fun Int.px2dp(): Int {
    return this.toFloat().px2dp()
}

/**
 * 带动画跳转
 */
fun Activity.startAnimationActivity(intent: Intent, view: View) {
    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, this.getString(R.string.transition_anim))
    startActivity(intent, options.toBundle())
}


/**
 * 隐藏虚拟导航栏
 */
fun Activity.hideBottomUIMenu() {
    val decorView = this.window.decorView
    val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
    decorView.systemUiVisibility = uiOptions
}







