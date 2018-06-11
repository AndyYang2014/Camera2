package com.andyyang.camera2.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andyyang.camera2.R
import com.andyyang.camera2.adapter.holder.ViewHolder
import com.andyyang.camera2.displayUrl
import kotlinx.android.synthetic.main.item_main.view.*

/**
 * Created by AndyYang
 * date:2018/3/19.
 * mail:andyyang2014@126.com
 */
class MainAdapter(val content: Context) : BaseAdapter<ViewHolder>() {
    val data = ArrayList<String>()

    override fun onBindView(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.main_item_img.displayUrl(data[position])
        viewHolder.itemView.tag = position
        viewHolder.itemView.setOnClickListener {
            itemClick?.invoke(it,position)
        }
    }

    var itemClick: ((View,Int) -> Unit)? = null

    fun update(list: ArrayList<String>) {
        Log.e("list", list[0])
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        return ViewHolder(LayoutInflater.from(content).inflate(R.layout.item_main, parent, false))
    }
}