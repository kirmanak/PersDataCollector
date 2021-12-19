package com.example.testapp.face

import androidx.camera.core.CameraSelector
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.FaceDetectionResultContract
import com.example.testapp.R
import com.example.testapp.base.BaseCameraCaptureFragment
import com.example.testapp.databinding.FmtFaceDetectionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class FaceDetectionFragment : BaseCameraCaptureFragment<File>(R.layout.fmt_face_detection) {

    @Inject
    lateinit var faceDetectionCallback: FaceDetectionImageCaptureCallback
    private val binding by viewBinding(FmtFaceDetectionBinding::bind)
    override val progress get() = binding.progress
    override val captureButton get() = binding.cameraCaptureButton
    override val previewView get() = binding.viewFinder
    override val captureCallback get() = faceDetectionCallback
    override val cameraSelector by lazy { CameraSelector.DEFAULT_FRONT_CAMERA }

    override fun retryDirection(text: String) = FaceDetectionFragmentDirections.faceToRetry(text)

    override fun createResultsIntent(data: File?) = FaceDetectionResultContract.createResult(data)
}