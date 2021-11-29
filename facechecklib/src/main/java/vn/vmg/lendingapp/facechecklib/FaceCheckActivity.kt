package vn.vmg.lendingapp.facechecklib

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import vn.vmg.lendingapp.facechecklib.model.FrontCheckRequest
import vn.vmg.lendingapp.facechecklib.retrofit.BaseRetrofit
import java.io.File
import java.util.concurrent.Executors

class FaceCheckActivity : AppCompatActivity() {
    companion object {
        var mFillBank = MutableLiveData<FrontCheckRequest>()
    }

    private var timeInMilliseconds = 0L
    private var startTime: Long = SystemClock.uptimeMillis()
    private var updatedTime: Long = 0L
    private var timeSwapBuff: Long = 0L
    private val customHandler = Handler(Looper.getMainLooper())
    private var RC_PERMISSION = 101
    var isRecording = false
    private var videoLength: Int = 0
    private var CAMERA_PERMISSION = Manifest.permission.CAMERA
    private var RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    val TAG: String = FaceCheckActivity::class.java.simpleName
    val mBaseRetrofit = BaseRetrofit()
    var imgCapture = findViewById<ImageView>(R.id.imgCapture)
    var textCounter = findViewById<TextView>(R.id.textCounter)
    var textStep = findViewById<TextView>(R.id.textStep)
    var cameraView = findViewById<TextureView>(R.id.camera_view)
    var videoCapture: VideoCapture? = null
    val videoCaptureConfigBuilder = VideoCaptureConfig.Builder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_check)

        initView()
//        handleData()
//        initListener()
    }

    private fun initView() {
        val recordFiles = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_MOVIES)
        val storageDirectory = recordFiles[0]
        val videoRecordingFilePath =
            "${storageDirectory.absoluteFile}/${System.currentTimeMillis()}_video.mp4"
        if (checkPermissions()) startCameraSession() else requestPermissions()


        imgCapture.alpha = 1.0f

        imgCapture.setOnClickListener {
            if (!isRecording) {
                isRecording = true
                Toast.makeText(this, "Bắt đầu ghi hình", Toast.LENGTH_SHORT).show()
                startTime = SystemClock.uptimeMillis()
                customHandler.postDelayed(updateTimerThread, 0)
                recordVideo(videoRecordingFilePath)
                textCounter.visibility = View.VISIBLE
            } else {
                textCounter.visibility = View.GONE
                isRecording = false
                videoCapture?.stopRecording()
            }
        }
    }

    val executors = Executors.newSingleThreadExecutor()
    private fun recordVideo(videoRecordingFilePath: String?) {
        val file = File(videoRecordingFilePath ?: "")
        val contextCompat = ContextCompat.getMainExecutor(this)
        videoCapture?.startRecording(
            file,
            executors,
            object : VideoCapture.OnVideoSavedListener {
                override fun onVideoSaved(file: File) {
                    Handler(Looper.getMainLooper()).post {
                        Log.d(TAG, "onVideoSaved $videoRecordingFilePath")
                        BaseRetrofit().uploadFile(
                            "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwOTg1NzMzNDg3IiwiaWF0IjoxNjM3OTQ1MDQ2LCJleHAiOjE2MzgwMzE0NDZ9.J4jFqCg0uXsEUrTPyg9JQ2RBboQB8DEFRYw6rvWVVvHdRNRZRmnLvy_M_ILo0XG7G7qXWtUXw227CzlaSa3B6w",
                                    file,
                            "211126164533874"
                        )
//                        layoutLoading.visibility = View.VISIBLE
                    }

                }

                override fun onError(
                    videoCaptureError: VideoCapture.VideoCaptureError,
                    message: String,
                    cause: Throwable?
                ) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@FaceCheckActivity, "Lưu video lỗi", Toast.LENGTH_SHORT)
                            .show()
                        Log.e(TAG, "onError $videoCaptureError $message")
                    }

                }
            }
        )
        /*binding.cameraView.startRecording(
            file,
            contextCompat,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(file: File) {
                    Log.d(TAG, "onVideoSaved $videoRecordingFilePath")
                    mVM.uploadImage(file)
                    binding.layoutLoading.visible()
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Toast.makeText(this@FaceCheckActivity, "Lưu video lỗi", Toast.LENGTH_SHORT)
                        .show()
                    Log.e(TAG, "onError $videoCaptureError $message")
                }
            })*/
    }

    private val updateTimerThread: Runnable by lazy {
        object : Runnable {
            override fun run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime
                updatedTime = timeSwapBuff + timeInMilliseconds
                var secs = (updatedTime / 1000).toInt()
                val mins = secs / 60
                val hrs = mins / 60
                secs %= 60
                textCounter.text =
                    String.format("%02d", mins) + ":" + String.format("%02d", secs)
                if (secs in 3..6) {
                    textStep.text = "QUAY TRÁI"
                } else if (secs > 6) {
                    textStep.text = "QUAY PHẢI"
                } else if (secs > 9) {
                    textStep.text = "NHÁY MẮT"
                } else {
                    textStep.text = "HƯỚNG THẲNG"
                }

                videoLength = secs
                if (videoLength > 12 && isRecording) {
                    textCounter.visibility = View.GONE
                    isRecording = false
                    //binding.cameraView.stopRecording()
                    videoCapture?.stopRecording()
                }
                customHandler.postDelayed(this, 0)
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(CAMERA_PERMISSION, RECORD_AUDIO_PERMISSION),
            RC_PERMISSION
        )
    }

    private fun checkPermissions(): Boolean {
        return ((ActivityCompat.checkSelfPermission(
            this,
            CAMERA_PERMISSION
        )) == PackageManager.PERMISSION_GRANTED
                && (ActivityCompat.checkSelfPermission(
            this,
            CAMERA_PERMISSION
        )) == PackageManager.PERMISSION_GRANTED)
    }

    private fun startCameraSession() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //binding.cameraView.bindToLifecycle(this)
        CameraX.unbindAll()
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(getWidthScreen() * 3 / 4, getWidthScreen()))
            setLensFacing(CameraX.LensFacing.FRONT)
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = cameraView.parent as ViewGroup
            parent.removeView(cameraView)
            parent.addView(cameraView, 0)

            cameraView.setSurfaceTexture(it.surfaceTexture)
            updateTransform()
        }

        videoCaptureConfigBuilder.apply {
            setLensFacing(CameraX.LensFacing.FRONT)

        }
        videoCapture = VideoCapture(videoCaptureConfigBuilder.build())
        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, videoCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = cameraView.width / 2f
        val centerY = cameraView.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (cameraView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        cameraView.setTransform(matrix)
    }

    fun getWidthScreen(): Int {
        val displayMetrics = DisplayMetrics()
        (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getHeightScreen(): Int {
        val displayMetrics = DisplayMetrics()
        (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}