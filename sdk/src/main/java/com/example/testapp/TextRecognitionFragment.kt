package com.example.testapp

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.databinding.FmtTextRecognitionBinding
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.guava.await
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TextRecognitionFragment : Fragment(R.layout.fmt_text_recognition) {

    private val binding by viewBinding(FmtTextRecognitionBinding::bind)
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.v("onViewCreated() called with: view = $view, savedInstanceState = $savedInstanceState")
        binding.cameraCaptureButton.setOnClickListener { takePhoto() }
    }

    override fun onResume() {
        super.onResume()
        Timber.v("onResume() called")
        // Request camera permissions
        if (allPermissionsGranted()) {
            lifecycleScope.launchWhenResumed { startCamera() }
        } else {
            Timber.i("onCreate: requesting camera permission")
            requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
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
        textCallback.result.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }
        val capture = imageCapture
        val executor = cameraExecutor
        if (capture != null && executor != null) {
            capture.takePicture(executor, textCallback)
        } else {
            Timber.w("takePhoto: either capture or executor is null")
        }
    }

    private suspend fun startCamera() {
        Timber.v("startCamera() called")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
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
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Timber.e(exc, "startCamera: can't start camera")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("onDestroy() called")
        cameraExecutor?.shutdownNow()
        cameraExecutor = null
    }

    private fun allPermissionsGranted(): Boolean {
        Timber.v("allPermissionsGranted() called")
        val all = REQUIRED_PERMISSIONS.all {
            checkSelfPermission(requireContext(), it) == PERMISSION_GRANTED
        }
        Timber.v("allPermissionsGranted() returned: $all")
        return all
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}