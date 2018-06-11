package com.andyyang.camera2.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.andyyang.camera2.PermissionHelper
import com.andyyang.camera2.R
import com.andyyang.camera2.adapter.MainAdapter
import com.andyyang.camera2.startAnimationActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ALBUM = 1001
    private val REQUEST_CODE_CAMERA = 1002
    private lateinit var mAdapter: MainAdapter
    private var mPermissionHelper: PermissionHelper? = null
    private val list = ArrayList<String>()
    private val permissionModels by lazy {
        arrayOf(PermissionHelper.PermissionModel("相机", Manifest.permission.CAMERA, "我们需要获取相机权限拍照", 108),
                PermissionHelper.PermissionModel("存储", Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要获取文件存储权限为您保存照片", 109))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
    }

    private fun initPermission() {
        mPermissionHelper = PermissionHelper(this)
        mPermissionHelper!!.onApplyPermission = {
            initView()
        }
        mPermissionHelper!!.requestPermissions(permissionModels)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mPermissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initView() {
        mAdapter = MainAdapter(this)
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this@MainActivity, 0, false)
            adapter = mAdapter
        }
        mAdapter.itemClick = { view, pos ->
            val intent = Intent(this@MainActivity, ImageActivity::class.java)
            intent.putExtra("image", list[pos])
            startAnimationActivity(intent, view)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.album -> {
                val intent = Intent(this@MainActivity, ImageSelecteActivity::class.java)
                intent.putExtra("type", ImageSelecteActivity.IMAGE_TYPE_FILES)
                intent.putExtra("sizetype", 9)
                startActivityForResult(intent, REQUEST_CODE_ALBUM)
            }
            R.id.camera -> {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                intent.putExtra("sizetype", 9)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
            R.id.camera2 -> {
                val intent = Intent(this@MainActivity, Camera2Activity::class.java)
                intent.putExtra("sizetype", 9)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPermissionHelper?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                val listExtra = data!!.getStringArrayListExtra("files")
                list.addAll(listExtra)
                mAdapter.update(listExtra)
            }
        }
    }
}
