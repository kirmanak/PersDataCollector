package com.example.testapp.text

import android.app.Activity
import androidx.camera.core.CameraSelector
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.R
import com.example.testapp.TextRecognitionResultContract
import com.example.testapp.base.BaseCameraCaptureFragment
import com.example.testapp.databinding.FmtTextRecognitionBinding
import com.example.testapp.retry.RetryDialogFragment.Companion.retryDialogResult
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

    override suspend fun processImageCaptureResult(result: Result<String>) {
        val exception = result.exceptionOrNull()
        if (exception != null) {
            val retryText = "${exception.message}. Do you want to try again?"
            findNavController().navigate(TextRecognitionFragmentDirections.textToRetry(retryText))
            if (retryDialogResult()) {
                Timber.d("processImageCaptureResult: user wants to retry")
                return
            }
        }

        val resultsIntent = TextRecognitionResultContract.createResult(result.getOrNull())
        with(requireActivity()) {
            setResult(Activity.RESULT_OK, resultsIntent)
            finish()
        }
    }
}