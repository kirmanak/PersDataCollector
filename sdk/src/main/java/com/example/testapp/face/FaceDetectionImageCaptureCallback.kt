package com.example.testapp.face

import android.graphics.Bitmap
import com.example.testapp.await
import com.example.testapp.base.BaseImageCaptureCallback
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceDetectionImageCaptureCallback @Inject constructor(
    private val faceDetector: FaceDetector,
) : BaseImageCaptureCallback<File>() {

    override suspend fun processImage(image: InputImage): Result<File> = runCatching {
        Timber.v("processImage() called with: image = $image")
        val faces = faceDetector.process(image).await()
        check(faces.size == 1) { "Expected one face but found ${faces.size} faces" }
        val file = File.createTempFile("face", null)
        FileOutputStream(file).use {
            image.bitmapInternal?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }
        file
    }
}