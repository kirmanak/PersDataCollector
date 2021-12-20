package com.example.testapp

import android.app.Activity
import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class CameraActivityTest : BaseTest() {

    @ApplicationContext
    @Inject
    lateinit var context: Context

    @Test
    fun `when face detection intent is sent then start destination is face detection`() {
        FaceDetectionResultContract().onActivity<Unit, File?, CameraActivity>(Unit) {
            val navController = findNavController(R.id.nav_host)
            assertEquals(R.id.faceDetectionFragment, navController.graph.startDestination)
        }
    }

    @Test
    fun `when text recognition intent is sent then start destination is text recognition`() {
        TextRecognitionResultContract().onActivity<Unit, String?, CameraActivity>(Unit) {
            val navController = findNavController(R.id.nav_host)
            assertEquals(R.id.textRecognitionFragment, navController.graph.startDestination)
        }
    }

    private inline fun <I, O, A : Activity> ActivityResultContract<I, O>.onActivity(
        input: I, crossinline block: A.() -> Unit
    ) {
        ActivityScenario.launch<A>(createIntent(context, input)).use { scenario ->
            scenario.onActivity { block(it) }
        }
    }
}