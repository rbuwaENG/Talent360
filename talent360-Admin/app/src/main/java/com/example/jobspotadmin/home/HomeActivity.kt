package com.app.talent360.home

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import com.app.talent360.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}