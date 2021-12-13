package com.example.testapp

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import timber.log.Timber

class TextIdImageCapturedCallback(
    private val recognizer: TextRecognizer,
) : BaseImageCaptureCallback<Text>() {

    override suspend fun processImage(image: InputImage): Result<Text> {
        Timber.v("processImage() called with: image = $image")
        return runCatching { recognizer.process(image).await() }
    }
}