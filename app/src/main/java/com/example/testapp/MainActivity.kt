package com.example.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textLauncher = registerForActivityResult(TextRecognitionResultContract()) {
            Timber.d("onCreate: received text: $it")
        }
        val faceLauncher = registerForActivityResult(FaceDetectionResultContract()) {
            Timber.d("onCreate: received $it faces")
        }
        binding.btnReadText.setOnClickListener { textLauncher.launch(Unit) }
        binding.btnDetectFace.setOnClickListener { faceLauncher.launch(Unit) }
    }
}
