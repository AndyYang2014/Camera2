package com.andyyang.camera2.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import com.andyyang.camera2.R
import com.andyyang.camera2.adapter.ImageSelectAdapter
import com.andyyang.camera2.bean.ImageBean
import com.andyyang.camera2.view.GalleryPopupWindow
import kotlinx.android.synthetic.main.activity_image_selecte.*

import java.io.File
import java.util.*

/**
 * Created by AndyYang
 * date:2018/2/2.
 * mail:andyyang2014@126.com
 */

class ImageSelecteActivity : AppCompatActivity() {

    private var mPopupWindow: GalleryPopupWindow? = null
    private var mGroupMap = HashMap<String, ArrayList<String>>()
    private var list = ArrayList<ImageBean>()
    private var listPath = ArrayList<String>()
    private var listSelectedPath = ArrayList<String>()


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            getGalleryList()
            listPath.clear()
            val grouplist = mGroupMap["所有图片"]
            listPath.addAll(grouplist!!)
            listPath.reverse()
            adapter.update(listPath)
            if (mPopupWindow != null)
                mPopupWindow!!.notifyDataChanged()
        }
    }
    lateinit private var adapter: ImageSelectAdapter
    private var mPaths: ArrayList<String>? = null
    private var mType: Int = 0
    private var mSizeype: Int = 0
    private var mSelepath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_selecte)
        init()
        initClick()
    }

    private fun init() {
        mPaths = intent.getStringArrayListExtra("paths")
        mType = intent.getIntExtra("type", -1)
        mSizeype = intent.getIntExtra("sizetype", -1)

        when (mType) {
            IMAGE_TYPE_FILES -> {
                tv_allPic.visibility = View.VISIBLE
                getImages()
            }
            IMAGE_TYPE_CAMERA -> {
                tv_allPic.visibility = View.GONE
                for (path in mPaths!!) {
                    listPath.add(path)
                }
            }
        }
        rv.layoutManager = GridLayoutManager(this, 3)
        adapter = ImageSelectAdapter(this, listPath, mSizeype)
        rv.adapter = adapter

        adapter.onCheckedChanged = { isChecked, path, _, _ ->
            if (isChecked) {
                mSelepath = path
                listSelectedPath.add(path)
            } else {
                if (listSelectedPath.contains(path))
                    listSelectedPath.remove(path)
            }
            if (listSelectedPath.size == 0) {
                setButtonDisable()
            } else {
                setButtonEnable()
            }

        }

    }

    fun initClick() {
        tv_allPic.setOnClickListener {
            if (mPopupWindow == null) {
                mPopupWindow = GalleryPopupWindow(this, list)
                mPopupWindow!!.onItemClick = {
                    setButtonDisable()
                    listPath.clear()
                    listSelectedPath.clear()
                    listPath.addAll(mGroupMap[it]!!)
                    listPath.reverse()
                    adapter.update(listPath)
                    tv_allPic.text = it
                }
            }
            mPopupWindow!!.showAtLocation(rv, Gravity.BOTTOM, 0, dp2px(50, this@ImageSelecteActivity))
        }
        bt_confirm.setOnClickListener {
            if (mSizeype == 1) {
                val intent = Intent()
                intent.putExtra("file", mSelepath)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (mSizeype == -1) {
                val intent1 = Intent()
                intent1.putStringArrayListExtra("files", listSelectedPath)
                intent1.putExtra("fileType", CLOUD_FILE_EXTEND_IMGS)
                setResult(Activity.RESULT_OK, intent1)
                finish()
            } else {
                val intent2 = Intent()
                intent2.putStringArrayListExtra("files", listSelectedPath)
                setResult(Activity.RESULT_OK, intent2)
                finish()
            }
        }

    }

    private fun setButtonEnable() {
        bt_confirm.setBackgroundResource(R.drawable.selector_bt)
        bt_confirm.setTextColor(Color.parseColor("#FFFFFF"))
        bt_confirm.isEnabled = true
        bt_confirm.text = "确定(" + listSelectedPath.size + ")"
    }

    private fun setButtonDisable() {
        bt_confirm.setBackgroundResource(R.drawable.shape_disable)
        bt_confirm.setTextColor(Color.parseColor("#0A91EF"))
        bt_confirm.isEnabled = false
        bt_confirm.text = "确定"
    }

    private fun getImages() {
        Thread(Runnable {
            val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val mContentResolver = this@ImageSelecteActivity.contentResolver

            val mCursor = mContentResolver.query(mImageUri, null, null, null,
                    MediaStore.Images.Media.DATE_MODIFIED) ?: return@Runnable
            val listAllPic = ArrayList<String>()
            while (mCursor.moveToNext()) {
                val path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA))

                val parentName = File(path).parentFile.name
                listAllPic.add(path)

                if (!mGroupMap.containsKey(parentName)) {
                    val chileList = ArrayList<String>()
                    chileList.add(path)
                    mGroupMap.put(parentName, chileList)
                } else {
                    mGroupMap[parentName]!!.add(path)
                }
            }
            mGroupMap.put("所有图片", listAllPic)
            mHandler.sendEmptyMessage(0)
            mCursor.close()
        }).start()


    }

    private fun getGalleryList() {
        val iterator = mGroupMap.entries.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val imageBean = ImageBean(next.value[0], next.key, next.value.size)
            if (next.key == "所有图片") list.add(0, imageBean) else list.add(imageBean)
        }
    }

    companion object {
        val CLOUD_FILE_EXTEND_IMGS = 15
        val IMAGE_TYPE_FILES = 3005
        val IMAGE_TYPE_CAMERA = 3006

        fun dp2px(dp: Int, context: Context): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                    context.resources.displayMetrics).toInt()
        }
    }


}
