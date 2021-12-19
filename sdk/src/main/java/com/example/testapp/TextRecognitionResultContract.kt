package com.example.testapp

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import timber.log.Timber

class TextRecognitionResultContract : ActivityResultContract<Unit, String?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        Timber.v("createIntent() called with: context = $context, input = $input")
        return Intent(context, CameraActivity::class.java).apply {
            putExtra(CameraActivity.DETECTION_MODE_EXTRA, CameraActivity.DetectionMode.TEXT)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        Timber.v("parseResult() called with: resultCode = $resultCode, intent = $intent")
        return intent?.getStringExtra(RECOGNIZED_TEXT_EXTRA)
    }

    companion object {
        private const val RECOGNIZED_TEXT_EXTRA = "RECOGNIZED_TEXT_EXTRA"

        fun createResult(text: String?): Intent {
            Timber.v("createResult() called with: text = $text")
            return Intent().apply { putExtra(RECOGNIZED_TEXT_EXTRA, text) }
        }
    }
}