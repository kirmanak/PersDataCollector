package com.example.testapp.face

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.testapp.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

@HiltAndroidTest
class ImageToFileWriterTest : BaseTest() {

    @Test
    fun `when write image then format and quality are correct`() {
        val format = Bitmap.CompressFormat.PNG
        val quality = 42
        val subject = ImageToFileWriter(createConfig(format, quality))
        val bitmap = spyk(createBitmap())
        runBlocking { subject.writeImageToTempFile(bitmap) }
        verify { bitmap.compress(format, quality, any()) }
    }

    @Test
    fun `when write image then file name starts with prefix`() {
        val config = createConfig(prefix = "test_")
        val subject = ImageToFileWriter(config)
        val actual = runBlocking { subject.writeImageToTempFile(createBitmap()) }
        assertTrue(actual.name.startsWith("test_"))
    }

    private fun createBitmap(): Bitmap = BitmapFactory.decodeFile("fake path")

    private fun createConfig(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100,
        prefix: String = "face_"
    ) = ImageToFileWriter.ImageToFileWriterConfig(format, quality, prefix)
}