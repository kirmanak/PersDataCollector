package com.example.testapp.face

import com.example.testapp.base.BaseImageCaptureCallback
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceDetectionImageCaptureCallback @Inject constructor(
    private val faceDetector: FaceDetector,
    private val imageToFileWriter: ImageToFileWriter,
) : BaseImageCaptureCallback<File>() {

    override suspend fun processImage(image: InputImage): Result<File> = runCatching {
        Timber.v("processImage() called with: image = $image")
        val bitmap = checkNotNull(image.bitmapInternal) { "Can't save image from camera" }
        val facesCount = faceDetector.process(image).await().size
        check(facesCount == 1) { "Expected one face but found $facesCount faces" }
        val file = imageToFileWriter.writeImageToTempFile(bitmap)
        file
    }
}