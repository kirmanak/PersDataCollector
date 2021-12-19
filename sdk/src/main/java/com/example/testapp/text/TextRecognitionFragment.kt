package com.example.testapp.text

import android.app.Activity
import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testapp.R
import com.example.testapp.TextRecognitionResultContract
import com.example.testapp.base.BaseCameraCaptureFragment
import com.example.testapp.databinding.FmtTextRecognitionBinding
import com.example.testapp.retry.RetryDialogFragment.Companion.retryDialogResult
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
            lifecycleScope.launch {
                result.receiveAsFlow().collect(::onTextRecognitionResult)
            }
        }

    private suspend fun onTextRecognitionResult(result: Result<Text>) {
        Timber.v("onTextRecognitionResult() called with: result = $result")
        val text = result.getOrNull()?.text?.takeUnless { it.isBlank() }.orEmpty()
        if (text.isEmpty()) {
            val retryText = "The picture doesn't contain any text. Do you want to try again?"
            findNavController().navigate(TextRecognitionFragmentDirections.textToRetry(retryText))
            if (retryDialogResult()) {
                Timber.d("onTextRecognitionResult: user wants to retry")
                return
            }
        }

        val resultsIntent = TextRecognitionResultContract.createResult(text)
        with(requireActivity()) {
            setResult(Activity.RESULT_OK, resultsIntent)
            finish()
        }
    }
}