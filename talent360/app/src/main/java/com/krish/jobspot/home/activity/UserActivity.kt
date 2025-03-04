package com.app.talent360.home.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.talent360.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private var _binding: ActivityUserBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}