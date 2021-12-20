package com.example.testapp.face

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.testapp.*
import com.google.mlkit.vision.face.FaceDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(value = [FaceDetectionModule::class])
class FaceDetectionFragmentTest : BaseTest() {

    @ApplicationContext
    @Inject
    lateinit var context: Context

    @BindValue
    @RelaxedMockK
    lateinit var faceDetector: FaceDetector

    @BindValue
    val imageWriterConfig = FaceDetectionModule.provideImageToFileWriterConfig()

    @Test
    fun `when launched capture face text is displayed`() {
        withFragment {
            onView(withId(R.id.camera_capture_button)).check(matches(withText(R.string.btn_capture_face)))
        }
    }

    private inline fun withFragment(crossinline block: FaceDetectionFragment.() -> Unit) {
        FaceDetectionResultContract().onActivity<Unit, File?, CameraActivity>(Unit, context) {
            (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment)
                .childFragmentManager
                .fragments
                .filterIsInstance<FaceDetectionFragment>()
                .first()
                .block()
        }
    }
}