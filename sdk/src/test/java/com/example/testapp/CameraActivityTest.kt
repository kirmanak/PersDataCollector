package com.example.testapp

import android.content.Context
import androidx.navigation.findNavController
import com.example.testapp.face.FaceDetectionModule
import com.example.testapp.text.TextRecognitionModule
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(value = [FaceDetectionModule::class, TextRecognitionModule::class])
class CameraActivityTest : BaseTest() {

    @ApplicationContext
    @Inject
    lateinit var context: Context

    @BindValue
    @RelaxedMockK
    lateinit var faceDetector: FaceDetector

    @BindValue
    @RelaxedMockK
    lateinit var textRecognizer: TextRecognizer

    @BindValue
    val imageWriterConfig = FaceDetectionModule.provideImageToFileWriterConfig()

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

}