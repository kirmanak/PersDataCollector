package com.example.testapp.text

import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.BaseCameraCaptureFragment
import com.example.testapp.R
import com.example.testapp.databinding.FmtTextRecognitionBinding
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TextRecognitionFragment : BaseCameraCaptureFragment(R.layout.fmt_text_recognition) {

    @Inject
    lateinit var textRecognitionCallback: TextRecognitionImageCaptureCallback
    private val binding by viewBinding(FmtTextRecognitionBinding::bind)
    override val captureButton: Button get() = binding.cameraCaptureButton
    override val previewView: PreviewView get() = binding.viewFinder
    override val cameraSelector: CameraSelector by lazy { CameraSelector.DEFAULT_BACK_CAMERA }
    override val captureCallback: ImageCapture.OnImageCapturedCallback
        get() = textRecognitionCallback.apply {
            result.observe(viewLifecycleOwner, ::onTextRecognitionResult)
        }

    private fun onTextRecognitionResult(result: Result<Text>) {
        Timber.v("onTextRecognitionResult() called with: result = $result")
    }
}