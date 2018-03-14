package com.andyyang.camera2.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.andyyang.camera2.adapter.BaseAdapter.BaseViewHolder

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

abstract class BaseAdapter<in T : BaseViewHolder> : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val index = position
        holder.view.setOnClickListener { onItemClick?.invoke(index) }

        onBindView(holder as T, position)
    }

    protected abstract fun onBindView(viewHolder: T, position: Int)

    open class BaseViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    var onItemClick: ((Int) -> Unit)? = null


}

