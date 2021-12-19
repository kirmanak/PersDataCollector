package com.example.testapp.face

import com.example.testapp.base.BaseImageCaptureCallback
import com.example.testapp.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceDetectionImageCaptureCallback @Inject constructor (
    private val faceDetector: FaceDetector,
) : BaseImageCaptureCallback<List<Face>>() {

    override suspend fun processImage(image: InputImage): Result<List<Face>> {
        Timber.v("processImage() called with: image = $image")
        return runCatching { faceDetector.process(image).await() }
    }
}