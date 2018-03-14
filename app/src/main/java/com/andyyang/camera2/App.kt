package com.andyyang.camera2

import android.app.Application
import kotlin.properties.Delegates

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        var context by Delegates.notNull<App>()
    }


}
