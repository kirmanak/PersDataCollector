package com.example.testapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import timber.log.Timber

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
abstract class BaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    open fun setUp() {
        MockKAnnotations.init(this)
        hiltRule.inject()
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpTimber() {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    println("$priority $tag $message")
                    t?.printStackTrace()
                }
            })
        }
    }
}