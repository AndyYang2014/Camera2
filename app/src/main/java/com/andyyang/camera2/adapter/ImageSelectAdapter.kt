package com.andyyang.camera2.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import com.andyyang.camera2.R
import com.andyyang.camera2.adapter.holder.ViewHolder
import com.andyyang.camera2.ImageLoader
import com.andyyang.camera2.showToast
import kotlinx.android.synthetic.main.item_image_select.view.*
import java.util.*

/**
 * Created by AndyYang
 * date:2017/7/28.
 * mail:andyyang2014@126.com
 */
class ImageSelectAdapter(private val context: Context, list: List<String>, size: Int) : BaseAdapter<ViewHolder>() {

    private val list = ArrayList<String>()
    private val listChecked = ArrayList<Boolean>()
    private var size: Int = -1
    var onCheckedChanged: ((Boolean, String, CheckBox, Int) -> (Unit))? = { _, _, _, _ -> }

    init {
        this.size = size
        this.list.addAll(list)
        setListCheched(list)
    }

    override fun onBindView(holder: ViewHolder, position: Int) {
        ImageLoader.loadImage(context, "file://" + list[position], holder.itemView.iv_itemImageSelect)
        holder.itemView.cb_itemImageSelect.isChecked = listChecked[position]
        holder.itemView.setOnClickListener {
            if (size == -1) setCheck(holder, position) else {
                val j = listChecked.count { it }
                if (j == size && !listChecked[position]) {
                    context.showToast("最多只能选择" + size + "张图片")
                } else {
                    setCheck(holder, position)
                }
            }
        }
    }

    private fun setCheck(holder: ViewHolder, position: Int) {
        holder.itemView.cb_itemImageSelect.isChecked = !holder.itemView.cb_itemImageSelect.isChecked
        listChecked[position] = holder.itemView.cb_itemImageSelect.isChecked
        onCheckedChanged?.invoke(holder.itemView.cb_itemImageSelect.isChecked, list[position], holder.itemView.cb_itemImageSelect, position)
    }

    fun update(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
        setListCheched(list)
        notifyDataSetChanged()
    }

    private fun setListCheched(list: List<String>) {
        listChecked.clear()
        for (i in list.indices) {
            listChecked.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_select, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
