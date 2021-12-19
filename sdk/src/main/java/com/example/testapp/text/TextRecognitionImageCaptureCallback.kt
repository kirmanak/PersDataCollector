package com.example.testapp.text

import com.example.testapp.await
import com.example.testapp.base.BaseImageCaptureCallback
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextRecognitionImageCaptureCallback @Inject constructor(
    private val recognizer: TextRecognizer,
) : BaseImageCaptureCallback<String>() {

    override suspend fun processImage(image: InputImage): Result<String> = runCatching {
        Timber.v("processImage() called with: image = $image")
        val text = recognizer.process(image).await().text
        check(text.isNotEmpty()) { "The picture doesn't contain any text" }
        text
    }
}