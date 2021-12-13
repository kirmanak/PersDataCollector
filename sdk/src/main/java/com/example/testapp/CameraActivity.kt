package com.example.testapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.testapp.databinding.ActivityCameraBinding
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.guava.await
import timber.log.Timber
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var binding: ActivityCameraBinding
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("onCreate() called with: savedInstanceState = $savedInstanceState")
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            lifecycleScope.launchWhenResumed { startCamera() }
        } else {
            Timber.i("onCreate: requesting camera permission")
            requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
        binding.cameraCaptureButton.setOnClickListener { takePhoto() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.v("onRequestPermissionsResult() called with: requestCode = $requestCode, permissions = $permissions, grantResults = $grantResults")
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.all { it == PERMISSION_GRANTED }) {
            lifecycleScope.launchWhenResumed { startCamera() }
        } else {
            Timber.w("onRequestPermissionsResult: can't start camera, no permission")
        }
    }

    private fun takePhoto() {
        Timber.v("takePhoto() called")
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        // Set up image capture listener, which is triggered after photo has been taken
        val textCallback = TextRecognitionImageCaptureCallback(recognizer)
        textCallback.result.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
        imageCapture?.takePicture(cameraExecutor, textCallback)
    }

    private suspend fun startCamera() {
        Timber.v("startCamera() called")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // Used to bind the lifecycle of cameras to the lifecycle owner
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.await()

        // Preview
        val preview = Preview.Builder()
            .build()
            .apply { setSurfaceProvider(binding.viewFinder.surfaceProvider) }

        imageCapture = ImageCapture.Builder().build()

        // Select back camera as a default
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()
            // Bind use cases to camera
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } catch (exc: Exception) {
            Timber.e(exc, "startCamera: can't start camera")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("onDestroy() called")
        cameraExecutor.shutdownNow()
    }

    private fun allPermissionsGranted(): Boolean {
        Timber.v("allPermissionsGranted() called")
        val all = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PERMISSION_GRANTED
        }
        Timber.v("allPermissionsGranted() returned: $all")
        return all
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        fun start(context: Context) {
            context.startActivity(Intent(context, CameraActivity::class.java))
        }
    }
}
