package com.example.testapp

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraCaptureFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private var cameraExecutor: ExecutorService? = null
    private val imageCapture: ImageCapture by lazy { ImageCapture.Builder().build() }
    protected abstract val captureButton: Button
    protected abstract val previewView: PreviewView
    protected abstract val captureCallback: ImageCapture.OnImageCapturedCallback
    protected abstract val cameraSelector: CameraSelector

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            if (requestPermission(Manifest.permission.CAMERA)) startCamera()
            else Timber.w("onViewCreated: camera permission denied")
        }
        captureButton.setOnClickListener { takePhoto() }
    }

    private fun takePhoto() {
        Timber.v("takePhoto() called")
        val executor = cameraExecutor ?: Executors.newSingleThreadExecutor()
        cameraExecutor = executor
        imageCapture.takePicture(executor, captureCallback)
    }

    private suspend fun startCamera() {
        Timber.v("startCamera() called")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.await()
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }
        cameraProvider.apply {
            try {
                unbindAll()
                bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Timber.e(exc, "startCamera: can't start camera")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("onDestroy() called")
        cameraExecutor?.shutdownNow()
        cameraExecutor = null
    }
}