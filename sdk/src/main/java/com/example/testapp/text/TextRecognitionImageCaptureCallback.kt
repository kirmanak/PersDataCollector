package com.example.testapp.text

import com.example.testapp.await
import com.example.testapp.base.BaseImageCaptureCallback
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextRecognitionImageCaptureCallback @Inject constructor(
    private val recognizer: TextRecognizer,
) : BaseImageCaptureCallback<Text>() {

    override suspend fun processImage(image: InputImage): Result<Text> {
        Timber.v("processImage() called with: image = $image")
        return runCatching { recognizer.process(image).await() }
    }
}