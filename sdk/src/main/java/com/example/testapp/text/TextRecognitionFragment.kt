package com.example.testapp.text

import androidx.camera.core.CameraSelector
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.R
import com.example.testapp.TextRecognitionResultContract.Companion.createResult
import com.example.testapp.base.BaseCameraCaptureFragment
import com.example.testapp.databinding.FmtTextRecognitionBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TextRecognitionFragment : BaseCameraCaptureFragment<String>(R.layout.fmt_text_recognition) {

    @Inject
    lateinit var textRecognitionCallback: TextRecognitionImageCaptureCallback
    private val binding by viewBinding(FmtTextRecognitionBinding::bind)
    override val progress get() = binding.progress
    override val captureButton get() = binding.cameraCaptureButton
    override val previewView get() = binding.viewFinder
    override val cameraSelector by lazy { CameraSelector.DEFAULT_BACK_CAMERA }
    override val captureCallback get() = textRecognitionCallback

    override fun retryDirection(text: String) = TextRecognitionFragmentDirections.textToRetry(text)

    override fun createResultsIntent(data: String?) = createResult(data)
}