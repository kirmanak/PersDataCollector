package com.example.testapp

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Fragment.requestPermission(permission: String) = suspendCoroutine<Boolean> {
    if (checkSelfPermission(requireContext(), permission) == PERMISSION_GRANTED) {
        Timber.d("requestPermission: permission is already granted")
        it.resume(true)
    } else {
        val launcher = registerForActivityResult(RequestPermission()) { isGranted ->
            Timber.d("requestPermission: request result is $isGranted")
            it.resume(isGranted)
        }
        launcher.launch(permission)
    }
}
