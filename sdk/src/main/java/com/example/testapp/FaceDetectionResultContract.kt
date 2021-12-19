package com.example.testapp

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import timber.log.Timber

class FaceDetectionResultContract : ActivityResultContract<Unit, Int>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        Timber.v("createIntent() called with: context = $context, input = $input")
        return Intent(context, CameraActivity::class.java).apply {
            putExtra(CameraActivity.DETECTION_MODE_EXTRA, CameraActivity.DetectionMode.FACE)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        Timber.v("parseResult() called with: resultCode = $resultCode, intent = $intent")
        return intent?.getIntExtra(DETECTED_FACES_EXTRA, 0) ?: 0
    }

    companion object {
        private const val DETECTED_FACES_EXTRA = "DETECTED_FACES_EXTRA"

        fun createResult(faces: Int): Intent {
            Timber.v("createResult() called with: faces = $faces")
            return Intent().apply { putExtra(DETECTED_FACES_EXTRA, faces) }
        }
    }
}