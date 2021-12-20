package com.example.testapp.host

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.FaceDetectionResultContract
import com.example.testapp.TextRecognitionResultContract
import com.example.testapp.host.databinding.ActivityMainBinding
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
            binding.txtResult.text = it
        }
        val faceLauncher = registerForActivityResult(FaceDetectionResultContract()) { file ->
            Timber.d("onCreate: received $file faces")
            val uri = file?.let { Uri.fromFile(it) }
            binding.imageFace.setImageURI(uri)
        }
        binding.btnReadText.setOnClickListener { textLauncher.launch(Unit) }
        binding.btnDetectFace.setOnClickListener { faceLauncher.launch(Unit) }
    }
}