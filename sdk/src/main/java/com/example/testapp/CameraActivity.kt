package com.example.testapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
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

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            lifecycleScope.launchWhenResumed { startCamera() }
        } else {
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
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.all { it == PERMISSION_GRANTED }) {
            lifecycleScope.launchWhenResumed { startCamera() }
        }
    }

    private fun takePhoto() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        // Set up image capture listener, which is triggered after photo has been taken
        val textCallback = TextRecognitionImageCaptureCallback(recognizer)
        textCallback.result.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            textCallback,
        )
    }

    private suspend fun startCamera() {
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
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { p ->
        ContextCompat.checkSelfPermission(baseContext, p) == PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        fun start(context: Context) {
            context.startActivity(Intent(context, CameraActivity::class.java))
        }
    }
}
