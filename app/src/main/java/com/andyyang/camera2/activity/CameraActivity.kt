package com.andyyang.camera2.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import com.andyyang.camera2.*
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Created by AndyYang
 * date:2017/7/28.
 * mail:andyyang2014@126.com
 */

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var camera: Camera? = null
    private var mCurrentCameraId = 0
    private val mPaths = ArrayList<String>()
    private val sizetype by lazy {
        intent.getIntExtra("sizetype", -1)
    }
    private val mHolder by lazy {
        camera_surface.holder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideBottomUIMenu()
        setContentView(R.layout.activity_camera)
        init()
    }

    private fun init() {
        supportActionBar?.hide()
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mHolder.setKeepScreenOn(true)
    }


    private fun takePhoto() {
        camera_picture.isEnabled = false
        try {
            camera?.takePicture({ }, { _, _ -> }, { data, _ ->
                startPreview()
                saveToSDCard(data)
            })
        } catch (t: Throwable) {
            t.printStackTrace()
            showToast("拍照失败，请重试！")
            try {
                camera?.startPreview()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val numCams = Camera.getNumberOfCameras()
        if (numCams > 0) {
            if (camera == null) {
                camera = getCamera(mCurrentCameraId)
                startPreview()
            }
        }
    }


    override fun onPause() {
        releaseCamera()
        super.onPause()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.camera_picture -> takePhoto()
            R.id.camera_image -> inAlbum()
            R.id.camera_finsh -> finish()
            R.id.camera_flash -> turnLight()
            R.id.camera_flip -> switchCamera()
        }
    }


    private fun inAlbum() {
        if (mPaths.size == 0) {
            return
        }
        val intent = Intent(this, ImageSelecteActivity::class.java)
        intent.putStringArrayListExtra("paths", mPaths)
        intent.putExtra("type", ImageSelecteActivity.IMAGE_TYPE_CAMERA)
        intent.putExtra("sizetype", sizetype)
        startActivityForResult(intent, CAMERA_CAMERAACTIVITY)
    }

    fun saveToSDCard(data: ByteArray) {
        doAsync {
            val sdCard = Environment.getExternalStorageDirectory()
            val dir = File(sdCard.absolutePath + "/DCIM/Camera/")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, cameraPath)
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val picktruebitmap = setTakePicktrueOrientation(mCurrentCameraId, bitmap)
            saveBitmapFile(picktruebitmap, file.absolutePath)
            uiThread {
                mPaths.add(file.absolutePath)
                camera_image.displayUrl("file://" + file.absolutePath)
                camera_picture.isEnabled = true
            }
        }
    }

    fun setTakePicktrueOrientation(id: Int, bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(id, info)
        bitmap = rotaingImageView(id, info.orientation, bitmap)
        return bitmap
    }

    fun rotaingImageView(id: Int, angle: Int, bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        if (id == 1) {
            matrix.postScale(-1f, 1f)
        }
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true)
        return resizedBitmap
    }


    fun saveBitmapFile(bitmap: Bitmap, filepath: String): File {
        val file = File(filepath)
        try {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }


    private fun turnLight() {
        if (camera == null || camera?.parameters == null || camera!!.parameters.supportedFlashModes == null) {
            return
        }
        val parameters = camera!!.parameters
        val flashMode = camera!!.parameters.flashMode
        val supportedModes = camera!!.parameters
                .supportedFlashModes
        if (Camera.Parameters.FLASH_MODE_OFF == flashMode && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {// 关闭状态
            parameters.flashMode = Camera.Parameters.FLASH_MODE_ON
            camera!!.parameters = parameters
             camera_flash!!.setImageResource(R.drawable.camera_flash_on)
        } else if (Camera.Parameters.FLASH_MODE_ON == flashMode) {// 开启状态
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                 camera_flash.setImageResource(R.drawable.camera_flash_auto)
                camera!!.parameters = parameters
            } else if (supportedModes
                    .contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera_flash.setImageResource(R.drawable.camera_flash_off)
                camera!!.parameters = parameters
            }
        } else if (Camera.Parameters.FLASH_MODE_AUTO == flashMode && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera!!.parameters = parameters
             camera_flash.setImageResource(R.drawable.camera_flash_off)
        }
    }

    private fun releaseCamera() {
        camera?.let {
            it.setPreviewCallback(null)
            it.stopPreview()
            it.release()
        }
        camera = null
    }

    private fun startPreview() {
        try {
            setupCamera()
            camera?.setPreviewDisplay( camera_surface.holder)
            setCameraDisplayOrientation(this, mCurrentCameraId, camera!!)
            camera?.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
                .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    private fun setupCamera() {
        val parameters = camera!!.parameters

        if (parameters.supportedFocusModes.contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }

        val previewSize = findBestPreviewResolution()
        parameters.setPreviewSize(previewSize.width, previewSize.height)

        val pictrueSize = findBestPreviewResolution()
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height)

        camera!!.parameters = parameters
    }


    private fun switchCamera() {
        releaseCamera()
        mCurrentCameraId = (mCurrentCameraId + 1) % Camera.getNumberOfCameras()
        camera = getCamera(mCurrentCameraId)
        startPreview()
    }

    private fun getCamera(mCurrentCameraId: Int): Camera? {
        val camera: Camera?
        try {
            camera = Camera.open(mCurrentCameraId)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return camera
    }

    fun findBestPreviewResolution(): Camera.Size {

        val cameraParameters = camera!!.parameters
        val defaultPreviewResolution = cameraParameters.previewSize

        val rawSupportedSizes = cameraParameters.supportedPreviewSizes ?: return defaultPreviewResolution

        val supportedPreviewResolutions = ArrayList(rawSupportedSizes)
        Collections.sort(supportedPreviewResolutions, Comparator<Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            }
            if (bPixels > aPixels) {
                return@Comparator 1
            }
            0
        })

        val previewResolutionSb = StringBuilder()
        for (supportedPreviewResolution in supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ')
        }

        val screenAspectRatio = getScreenWidth().toDouble() / getScreenHeight()
        val it = supportedPreviewResolutions.iterator()
        while (it.hasNext()) {
            val supportedPreviewResolution = it.next()
            val width = supportedPreviewResolution.width
            val height = supportedPreviewResolution.height

            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove()
                continue
            }

            val isCandidatePortrait = width > height
            val maybeFlippedWidth = if (isCandidatePortrait) height else width
            val maybeFlippedHeight = if (isCandidatePortrait) width else height
            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove()
                continue
            }

            if (maybeFlippedWidth == getScreenWidth() && maybeFlippedHeight == getScreenHeight()) {
                return supportedPreviewResolution
            }
        }

        if (!supportedPreviewResolutions.isEmpty()) {
            val largestPreview = supportedPreviewResolutions[0]
            return largestPreview
        }

        return defaultPreviewResolution
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        camera?.stopPreview()
        camera?.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camera?.stopPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            camera?.setPreviewDisplay(holder)
        } catch (e: IOException) {
            camera?.release()
            camera = null
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_CAMERAACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    companion object {
        private val CAMERA_CAMERAACTIVITY = 88
        val CAMERA_TYPE_FILES = 3002
        private val MIN_PREVIEW_PIXELS = 1080 * 720
        private val MAX_ASPECT_DISTORTION = 0.15

        private // 0~11
        val cameraPath: String
            get() {
                val calendar = Calendar.getInstance()
                val sb = StringBuilder()
                sb.append("IMG")
                sb.append(calendar.get(Calendar.YEAR))
                val month = calendar.get(Calendar.MONTH) + 1
                sb.append(if (month < 10) "0" + month else month)
                val day = calendar.get(Calendar.DATE)
                sb.append(if (day < 10) "0" + day else day)
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                sb.append(if (hour < 10) "0" + hour else hour)
                val minute = calendar.get(Calendar.MINUTE)
                sb.append(if (minute < 10) "0" + minute else minute)
                val second = calendar.get(Calendar.SECOND)
                sb.append(if (second < 10) "0" + second else second)
                if (!File(sb.toString() + ".jpg").exists()) {
                    return sb.toString() + ".jpg"
                }

                val tmpSb = StringBuilder(sb)
                val indexStart = sb.length
                for (i in 1..Integer.MAX_VALUE - 1) {
                    tmpSb.append('(')
                    tmpSb.append(i)
                    tmpSb.append(')')
                    tmpSb.append(".jpg")
                    if (!File(tmpSb.toString()).exists()) {
                        break
                    }

                    tmpSb.delete(indexStart, tmpSb.length)
                }

                return tmpSb.toString()
            }
    }
}
