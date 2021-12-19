package com.example.testapp.face

import android.app.Activity
import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.FaceDetectionResultContract
import com.example.testapp.R
import com.example.testapp.base.BaseCameraCaptureFragment
import com.example.testapp.databinding.FmtFaceDetectionBinding
import com.example.testapp.retry.RetryDialogFragment.Companion.retryDialogResult
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
            lifecycleScope.launch {
                result.receiveAsFlow().collect(::onFaceDetectionResult)
            }
        }

    override val cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private suspend fun onFaceDetectionResult(result: Result<List<Face>>) {
        Timber.v("onFaceDetectionResult() called with: result = $result")
        val facesList = result.getOrDefault(emptyList())
        val size = facesList.size
        if (size != 1) {
            val text = "The picture contains $size faces, but one was expected." +
                    " Do you want to try again?"
            findNavController().navigate(FaceDetectionFragmentDirections.faceToRetry(text))
            if (retryDialogResult()) {
                Timber.d("onFaceDetectionResult: user wants to try again")
                return
            }
        }

        val resultsIntent = FaceDetectionResultContract.createResult(size)
        with(requireActivity()) {
            setResult(Activity.RESULT_OK, resultsIntent)
            finish()
        }
    }
}