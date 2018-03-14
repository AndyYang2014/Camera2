package com.andyyang.camera2.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.andyyang.camera2.R
import com.andyyang.camera2.activity.ImageSelecteActivity
import com.andyyang.camera2.adapter.GalleryAdapter
import com.andyyang.camera2.bean.ImageBean

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */
class GalleryPopupWindow(private val activity: Activity, private val list: List<ImageBean>) : PopupWindow(activity) {

    lateinit var mRecyclerView: RecyclerView
    lateinit var adapter: GalleryAdapter
    var onItemClick: ((String) -> Unit)? = {}

    init {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.popu_gallery, null)
        initView(contentView)

        val h = activity.windowManager.defaultDisplay.height
        val w = activity.windowManager.defaultDisplay.width
        this.contentView = contentView
        this.width = w
        this.height = ImageSelecteActivity.dp2px(350, activity)
        this.isFocusable = false
        this.isOutsideTouchable = true
        this.update()

        setBackgroundDrawable(ColorDrawable(0))
    }

    fun notifyDataChanged() {
        adapter.notifyDataSetChanged()
    }

    private fun initView(contentView: View) {
        mRecyclerView = contentView.findViewById<RecyclerView>(R.id.rv_gallery)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = GalleryAdapter(list, activity)
        mRecyclerView.adapter = adapter
        adapter.onItem = {
            onItemClick?.invoke(it)
        }
    }

}
