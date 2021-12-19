package com.example.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.databinding.AcCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private val binding by viewBinding(AcCameraBinding::bind, R.id.nav_host)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_camera)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val detectionMode = intent?.getSerializableExtra(DETECTION_MODE_EXTRA) as? DetectionMode
        Timber.d("onPostCreate: detection mode is $detectionMode")
        detectionMode?.let {
            val controller = binding.navHost.findNavController()
            controller.graph = controller.navInflater.inflate(R.navigation.nav_graph).apply {
                startDestination = when (detectionMode) {
                    DetectionMode.FACE -> R.id.faceDetectionFragment
                    DetectionMode.TEXT -> R.id.textRecognitionFragment
                }
            }
        } ?: run {
            Timber.w("onPostCreate: detection mode must be provided")
            finish()
        }
    }

    companion object {
        const val DETECTION_MODE_EXTRA = "DETECTION_MODE_EXTRA"
    }

    enum class DetectionMode { FACE, TEXT }
}
