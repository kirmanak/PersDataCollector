package com.example.testapp.base

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.testapp.requestPermission
import com.example.testapp.retry.RetryDialogFragment.Companion.retryDialogResult
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraCaptureFragment<T>(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private var cameraExecutor: ExecutorService? = null
    private val imageCapture: ImageCapture by lazy { ImageCapture.Builder().build() }

    protected abstract val captureButton: Button
    protected abstract val previewView: PreviewView
    protected abstract val progress: CircularProgressIndicator

    protected abstract val cameraSelector: CameraSelector
    protected abstract val captureCallback: BaseImageCaptureCallback<T>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            if (requestPermission(Manifest.permission.CAMERA)) startCamera()
            else Timber.w("onViewCreated: camera permission denied")
        }
        captureButton.setOnClickListener { captureImage() }
    }

    private suspend fun onImageCaptured(captureResult: Result<T>) {
        Timber.v("onImageCaptured() called with: captureResult = $captureResult")
        captureButton.isClickable = true
        progress.isVisible = false

        val wantToRetry = captureResult.exceptionOrNull()?.let {
            val text = "${it.message}. Do you want to try again?"
            findNavController().navigate(retryDirection(text))
            retryDialogResult()
        } ?: false

        if (!wantToRetry) {
            with(requireActivity()) {
                setResult(Activity.RESULT_OK, createResultsIntent(captureResult.getOrNull()))
                finish()
            }
        }
    }

    abstract fun retryDirection(text: String): NavDirections

    abstract fun createResultsIntent(data: T?): Intent

    private fun captureImage() {
        Timber.v("captureImage() called")
        captureButton.isClickable = false
        progress.isVisible = true
        val executor = cameraExecutor ?: run {
            val newExecutor = Executors.newSingleThreadExecutor()
            cameraExecutor = newExecutor
            newExecutor
        }
        lifecycleScope.launch {
            onImageCaptured(captureCallback.result.receiveAsFlow().first())
        }
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