package com.andyyang.camera2.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andyyang.camera2.R
import com.andyyang.camera2.adapter.holder.ViewHolder
import com.andyyang.camera2.bean.ImageBean
import com.andyyang.camera2.ImageLoader
import kotlinx.android.synthetic.main.item_gallery.view.*


/**
 * Created by AndyYang
 * date:2017/7/28.
 * mail:andyyang2014@126.com
 */
class GalleryAdapter(private var list: List<ImageBean>, private val context: Context) : BaseAdapter<ViewHolder>() {

    private var selectedPos: Int = 0
    var onItem: ((String) -> Unit)? = {}


    override fun onBindView(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
            onItem?.invoke(list[position].fileName)
        }
        if (position == selectedPos) holder.itemView.iv_itemGallery_check.visibility = View.VISIBLE else holder.itemView.iv_itemGallery_check.visibility = View.GONE
        holder.itemView.tv_itemGallery_count.text = list[position].count.toString() + "张"
        holder.itemView.tv_itemGallery_name.text = list[position].fileName
        ImageLoader.loadImage(context, "file://" + list[position].firstPicPath, holder.itemView.iv_itemGallery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
