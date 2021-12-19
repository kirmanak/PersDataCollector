package com.example.testapp

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import timber.log.Timber
import java.io.File

class FaceDetectionResultContract : ActivityResultContract<Unit, File?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        Timber.v("createIntent() called with: context = $context, input = $input")
        return Intent(context, CameraActivity::class.java).apply {
            putExtra(CameraActivity.DETECTION_MODE_EXTRA, CameraActivity.DetectionMode.FACE)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): File? {
        Timber.v("parseResult() called with: resultCode = $resultCode, intent = $intent")
        return intent?.getSerializableExtra(DETECTED_FACES_EXTRA) as? File
    }

    companion object {
        private const val DETECTED_FACES_EXTRA = "DETECTED_FACES_EXTRA"

        fun createResult(faceImage: File?): Intent {
            Timber.v("createResult() called with: faceImage = $faceImage")
            return Intent().apply { putExtra(DETECTED_FACES_EXTRA, faceImage) }
        }
    }
}