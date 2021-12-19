package com.example.testapp.retry

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.example.testapp.R
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

class RetryDialogFragment : DialogFragment() {

    private val args: RetryDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(args.retryText)
            .setPositiveButton(R.string.btn_retry) { _, _ ->
                Timber.d("onCreateDialog: positive click")
                setResult(true)
            }
            .setNegativeButton(R.string.btn_no) { _, _ ->
                Timber.d("onCreateDialog: negative click")
                setResult(false)
            }
        return builder.create()
    }

    private fun setResult(result: Boolean) {
        Timber.v("setResult() called with: result = $result")
        setFragmentResult(RETRY_REQUEST_KEY, bundleOf(ANSWER_KEY to result))
    }

    companion object {
        private const val RETRY_REQUEST_KEY = "RETRY_REQUEST_KEY"
        private const val ANSWER_KEY = "ANSWER_KEY"

        suspend fun Fragment.retryDialogResult(): Boolean = with(parentFragmentManager) {
            suspendCancellableCoroutine { cont ->
                clearFragmentResultListener(RETRY_REQUEST_KEY)
                Timber.d("retryDialogResult: setting fragment result listener")
                setFragmentResultListener(RETRY_REQUEST_KEY, viewLifecycleOwner, { _, it ->
                    Timber.d("retryDialogResult: on fragment result $it")
                    cont.resume(it.getBoolean(ANSWER_KEY))
                })
                cont.invokeOnCancellation {
                    Timber.d("retryDialogResult: on continuation cancel")
                    clearFragmentResultListener(RETRY_REQUEST_KEY)
                }
            }
        }
    }
}