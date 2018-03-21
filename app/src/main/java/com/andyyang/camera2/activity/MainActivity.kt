package com.andyyang.camera2.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.andyyang.camera2.R
import com.andyyang.camera2.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ALBUM = 1001
    private val REQUEST_CODE_CAMERA = 1002
    lateinit var mAdapter: MainAdapter
    private val list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        mAdapter = MainAdapter(this)
        with(recyclerview) {
            layoutManager = LinearLayoutManager(this@MainActivity, 0, false)
            adapter = mAdapter
        }
        mAdapter.itemClick = {
            val intent = Intent(this@MainActivity, ImageActivity::class.java)
            intent.putExtra("image", list[it])
            startActivity(intent)
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                val listExtra = data!!.getStringArrayListExtra("files")
                list.addAll(listExtra)
                mAdapter.update(listExtra)
            }
        }
    }
}
