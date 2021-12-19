package com.example.testapp.face

import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.BaseCameraCaptureFragment
import com.example.testapp.R
import com.example.testapp.databinding.FmtFaceDetectionBinding
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FaceDetectionFragment : BaseCameraCaptureFragment(R.layout.fmt_face_detection) {

    @Inject
    lateinit var faceDetectionCallback: FaceDetectionImageCaptureCallback
    private val binding by viewBinding(FmtFaceDetectionBinding::bind)
    override val captureButton: Button get() = binding.cameraCaptureButton
    override val previewView: PreviewView get() = binding.viewFinder
    override val captureCallback: ImageCapture.OnImageCapturedCallback
        get() = faceDetectionCallback.apply {
            result.observe(viewLifecycleOwner, ::onFaceDetectionResult)
        }
    override val cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private fun onFaceDetectionResult(result: Result<List<Face>>) {
        Timber.v("onFaceDetectionResult() called with: result = $result")
    }
}