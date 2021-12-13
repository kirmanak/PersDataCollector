package com.example.testapp

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Fragment.requestPermission(permission: String) = suspendCoroutine<Boolean> {
    Timber.v("requestPermission() called with: permission = $permission")
    if (checkSelfPermission(requireContext(), permission) == PERMISSION_GRANTED) {
        Timber.d("requestPermission: permission is already granted")
        it.resume(true)
    } else {
        Timber.d("requestPermission: requesting permission")
        val launcher = registerForActivityResult(RequestPermission()) { isGranted ->
            Timber.d("requestPermission: request result is $isGranted")
            it.resume(isGranted)
        }
        launcher.launch(permission)
    }
}

suspend fun <T> Task<T>.await() = suspendCoroutine<T> { cont ->
    addOnFailureListener { cont.resumeWithException(it) }
    addOnSuccessListener { cont.resume(it) }
}
