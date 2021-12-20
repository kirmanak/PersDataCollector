package com.example.testapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(
    application = HiltTestApplication_Application::class, instrumentedPackages = [
        // required to access final members on androidx.loader.content.ModernAsyncTask
        "androidx.loader.content"
    ]
)
@RunWith(AndroidJUnit4::class)
abstract class BaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    open fun setUp() {
        MockKAnnotations.init(this)
        hiltRule.inject()
    }
}