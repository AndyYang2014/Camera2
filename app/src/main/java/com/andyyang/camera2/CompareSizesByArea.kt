package com.andyyang.camera2

import android.util.Size
import java.lang.Long.signum
import java.util.*

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */
internal class CompareSizesByArea : Comparator<Size> {

    override fun compare(lhs: Size, rhs: Size) =
            signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)

}
