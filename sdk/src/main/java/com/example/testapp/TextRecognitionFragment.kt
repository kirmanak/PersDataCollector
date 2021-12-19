package com.example.testapp

import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.databinding.FmtTextRecognitionBinding
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import timber.log.Timber

class TextRecognitionFragment : BaseCameraCaptureFragment(R.layout.fmt_text_recognition) {

    private val recognizer: TextRecognizer
        get() = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val binding by viewBinding(FmtTextRecognitionBinding::bind)
    override val captureButton: Button get() = binding.cameraCaptureButton
    override val previewView: PreviewView get() = binding.viewFinder
    override val cameraSelector: CameraSelector by lazy { CameraSelector.DEFAULT_BACK_CAMERA }
    override val captureCallback: ImageCapture.OnImageCapturedCallback
        get() = TextRecognitionImageCaptureCallback(recognizer).apply {
            result.observe(viewLifecycleOwner, ::onTextRecognitionResult)
        }

    private fun onTextRecognitionResult(result: Result<Text>) {
        Timber.v("onTextRecognitionResult() called with: result = $result")
    }
}