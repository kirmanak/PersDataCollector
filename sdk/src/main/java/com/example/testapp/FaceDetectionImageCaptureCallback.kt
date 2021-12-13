package com.example.testapp

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import timber.log.Timber

class FaceDetectionImageCaptureCallback(
    private val faceDetector: FaceDetector,
) : BaseImageCaptureCallback<List<Face>>() {

    override suspend fun processImage(image: InputImage): Result<List<Face>> {
        Timber.v("processImage() called with: image = $image")
        val result = runCatching { faceDetector.process(image).await() }
        Timber.v("processImage() returned: $result")
        return result
    }
}