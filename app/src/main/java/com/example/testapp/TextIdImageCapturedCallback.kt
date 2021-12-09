package com.example.testapp

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer

class TextIdImageCapturedCallback(
    private val recognizer: TextRecognizer,
) : ImageCapture.OnImageCapturedCallback() {

    private val _lastRecognitionResult = MutableLiveData<Result<Text>>()
    val lastRecognitionResult: LiveData<Result<Text>> by ::_lastRecognitionResult

    @ExperimentalGetImage
    override fun onCaptureSuccess(proxy: ImageProxy) {
        Log.d(TAG, "onCaptureSuccess() called")
        super.onCaptureSuccess(proxy)
        try {
            val image = proxy.image ?: return
            val rotationDegrees = proxy.imageInfo.rotationDegrees
            val input = InputImage.fromMediaImage(image, rotationDegrees)
            recognizer.process(input)
                .addOnSuccessListener {
                    Log.d(TAG, "onCaptureSuccess: managed to extract ${it.text}")
                    _lastRecognitionResult.postValue(Result.success(it))
                }
                .addOnFailureListener {
                    Log.e(TAG, "onCaptureSuccess: can't process image", it)
                    _lastRecognitionResult.postValue(Result.failure(it))
                }
        } catch (e: Throwable) {
            Log.e(TAG, "onCaptureSuccess: can't analyze image", e)
            _lastRecognitionResult.postValue(Result.failure(e))
        } finally {
            proxy.close()
        }
    }

    override fun onError(exception: ImageCaptureException) {
        Log.d(TAG, "onError() called", exception)
    }

    companion object {
        private const val TAG = "TextIdImageCaptured"
    }
}