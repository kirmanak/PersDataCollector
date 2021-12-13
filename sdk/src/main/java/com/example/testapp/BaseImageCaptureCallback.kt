package com.example.testapp

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.runBlocking
import timber.log.Timber

abstract class BaseImageCaptureCallback<T> : ImageCapture.OnImageCapturedCallback() {

    private val _result = MutableLiveData<Result<T>>()
    val result: LiveData<Result<T>> by ::_result

    @ExperimentalGetImage
    final override fun onCaptureSuccess(proxy: ImageProxy) {
        super.onCaptureSuccess(proxy)
        Timber.v("onCaptureSuccess() called with: proxy = $proxy")

        val image = proxy.image
        if (image == null) {
            val exception = NullPointerException("Image is null")
            Timber.w(exception, "onCaptureSuccess: image is null")
            _result.postValue(Result.failure(exception))
            return
        }

        val rotationDegrees = proxy.imageInfo.rotationDegrees
        val input = InputImage.fromMediaImage(image, rotationDegrees)

        val res = try {
            runBlocking { processImage(input) }
        } catch (e: Throwable) {
            Timber.e(e, "onCaptureSuccess: can't process image")
            Result.failure(e)
        } finally {
            proxy.close()
        }

        _result.postValue(res)
    }

    final override fun onError(exception: ImageCaptureException) {
        super.onError(exception)
        Timber.e(exception, "onError: can't capture image")
        _result.postValue(Result.failure(exception))
    }

    abstract suspend fun processImage(image: InputImage): Result<T>
}