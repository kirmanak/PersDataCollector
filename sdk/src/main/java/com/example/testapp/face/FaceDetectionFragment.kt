package com.example.testapp.face

import android.app.Activity
import androidx.camera.core.CameraSelector
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.FaceDetectionResultContract
import com.example.testapp.R
import com.example.testapp.base.BaseCameraCaptureFragment
import com.example.testapp.databinding.FmtFaceDetectionBinding
import com.example.testapp.retry.RetryDialogFragment.Companion.retryDialogResult
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

    override suspend fun processImageCaptureResult(result: Result<File>) {
        val exception = result.exceptionOrNull()
        if (exception != null) {
            val text = "${exception.message}. Do you want to try again?"
            findNavController().navigate(FaceDetectionFragmentDirections.faceToRetry(text))
            if (retryDialogResult()) {
                Timber.d("processImageCaptureResult: user wants to try again")
                return
            }
        }

        val resultsIntent = FaceDetectionResultContract.createResult(result.getOrNull())
        with(requireActivity()) {
            setResult(Activity.RESULT_OK, resultsIntent)
            finish()
        }
    }
}