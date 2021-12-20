package com.example.testapp


import android.app.Activity
import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider

inline fun <I, O, A : Activity> ActivityResultContract<I, O>.onActivity(
    input: I,
    context: Context = ApplicationProvider.getApplicationContext(),
    crossinline block: A.() -> Unit
) {
    ActivityScenario.launch<A>(createIntent(context, input)).use { scenario ->
        scenario.onActivity { block(it) }
    }
}