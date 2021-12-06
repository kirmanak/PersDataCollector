package com.example.testapp

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer

class TextIdImageCapturedCallback(
    private val recognizer: TextRecognizer,
) : ImageCapture.OnImageCapturedCallback() {

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
                }
                .addOnFailureListener {
                    Log.e(TAG, "onCaptureSuccess: can't process image", it)
                }
        } catch (e: Throwable) {
            Log.e(TAG, "onCaptureSuccess: can't analyze image", e)
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