package com.example.testapp.face

import android.graphics.BitmapFactory
import com.example.testapp.BaseTest
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.inject.Inject

@UninstallModules(FaceDetectionModule::class)
@HiltAndroidTest
class FaceDetectionImageCaptureCallbackTest : BaseTest() {

    @BindValue
    @RelaxedMockK
    lateinit var writer: ImageToFileWriter

    @BindValue
    @RelaxedMockK
    lateinit var faceDetector: FaceDetector

    @RelaxedMockK
    lateinit var image: InputImage

    @Inject
    lateinit var subject: FaceDetectionImageCaptureCallback

    @Test
    fun `when process zero faces then fails`() {
        every { faceDetector.process(any<InputImage>()) } returns Tasks.forResult(listOfFaces(0))
        val actual = runBlocking { subject.processImage(image) }
        assertTrue(actual.isFailure)
    }

    @Test
    fun `when process two faces then throws`() {
        every { faceDetector.process(any<InputImage>()) } returns Tasks.forResult(listOfFaces(2))
        val actual = runBlocking { subject.processImage(image) }
        assertTrue(actual.isFailure)
    }

    @Test
    fun `when process one face then returns success`() {
        every { faceDetector.process(any<InputImage>()) } returns Tasks.forResult(listOfFaces(1))
        val actual = runBlocking { subject.processImage(image) }
        assertTrue(actual.isSuccess)
    }

    @Test
    fun `when process image without bitmap then throws`() {
        every { image.bitmapInternal } returns null
        val actual = runBlocking { subject.processImage(image) }
        assertTrue(actual.isFailure)
    }

    @Test
    fun `when process image with one face then calls image writer`() {
        every { faceDetector.process(any<InputImage>()) } returns Tasks.forResult(listOfFaces(1))
        val bitmap = BitmapFactory.decodeFile("test")
        every { image.bitmapInternal } returns bitmap
        runBlocking { subject.processImage(image) }
        coVerify { writer.writeImageToTempFile(bitmap) }
        confirmVerified(writer)
    }

    @Test
    fun `when process image with one face then returns file from writer`() {
        every { faceDetector.process(any<InputImage>()) } returns Tasks.forResult(listOfFaces(1))
        val file = File("test")
        coEvery { writer.writeImageToTempFile(any()) } returns file
        val actual = runBlocking { subject.processImage(image) }
        assertSame(file, actual.getOrNull())
    }

    private fun listOfFaces(size: Int): List<Face> = MutableList(size) { mockk(relaxed = true) }
}