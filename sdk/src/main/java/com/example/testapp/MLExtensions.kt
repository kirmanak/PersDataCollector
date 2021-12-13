package com.example.testapp

import com.google.android.gms.tasks.Task
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.await() = suspendCoroutine<T> { cont ->
    addOnFailureListener { cont.resumeWith(Result.failure(it)) }
    addOnSuccessListener { cont.resumeWith(Result.success(it)) }
}
