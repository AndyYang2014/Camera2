package com.andyyang.camera2.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.andyyang.camera2.R
import com.andyyang.camera2.displayUrl
import kotlinx.android.synthetic.main.activity_image.*

/**
 * Created by AndyYang
 * date:2018/3/21.
 * mail:andyyang2014@126.com
 */
class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_image)
        supportActionBar?.hide()
        image_view.displayUrl("file://" + intent.getStringExtra("image"))
    }
}