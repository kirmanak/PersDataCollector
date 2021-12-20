package com.example.testapp.face

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageToFileWriter @Inject constructor(
    private val config: ImageToFileWriterConfig,
) {

    suspend fun writeImageToTempFile(image: Bitmap): File = withContext(Dispatchers.IO) {
        File.createTempFile(config.prefix, null).also { file ->
            FileOutputStream(file).use { outputStream ->
                image.compress(config.format, config.quality, outputStream)
                outputStream.flush()
            }
        }
    }

    data class ImageToFileWriterConfig(
        val format: Bitmap.CompressFormat,
        val quality: Int,
        val prefix: String
    )
}