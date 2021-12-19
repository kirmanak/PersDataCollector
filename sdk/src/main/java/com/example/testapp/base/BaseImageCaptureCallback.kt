package com.example.testapp.base

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import timber.log.Timber

abstract class BaseImageCaptureCallback<T> : ImageCapture.OnImageCapturedCallback() {

    private val _result = Channel<Result<T>>()
    val result: ReceiveChannel<Result<T>> by ::_result

    @ExperimentalGetImage
    final override fun onCaptureSuccess(proxy: ImageProxy) {
        super.onCaptureSuccess(proxy)
        Timber.v("onCaptureSuccess() called with: proxy = $proxy")

        val image = proxy.image
        if (image == null) {
            val exception = NullPointerException("Image is null")
            Timber.w(exception, "onCaptureSuccess: image is null")
            runBlocking { _result.send(Result.failure(exception)) }
            return
        }

        val rotationDegrees = proxy.imageInfo.rotationDegrees
        val input = InputImage.fromMediaImage(image, rotationDegrees)

        runBlocking {
            val res = try {
                processImage(input)
            } catch (e: Throwable) {
                Timber.e(e, "onCaptureSuccess: can't process image")
                Result.failure(e)
            } finally {
                proxy.close()
            }
            _result.send(res)
        }
    }

    final override fun onError(exception: ImageCaptureException) {
        super.onError(exception)
        Timber.e(exception, "onError: can't capture image")
        runBlocking { _result.send(Result.failure(exception)) }
    }

    abstract suspend fun processImage(image: InputImage): Result<T>
}