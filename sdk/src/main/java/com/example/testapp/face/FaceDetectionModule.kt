package com.example.testapp.face

import android.graphics.Bitmap
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FaceDetectionModule {

    @Provides
    @Singleton
    fun provideFaceDetector(options: FaceDetectorOptions) = FaceDetection.getClient(options)

    @Provides
    @Singleton
    fun provideFaceDetectorOptions() = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()

    @Provides
    @Singleton
    fun provideImageToFileWriterConfig() = ImageToFileWriter.Config(
        format = Bitmap.CompressFormat.JPEG,
        quality = 100,
        prefix = "face"
    )
}