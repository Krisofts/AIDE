package com.krisoft.aide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krisoft.aide.databinding.ActivityMainBinding
import com.krisoft.aide.sdkmanager.ui.SdkManagerActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOpenSdkManager.setOnClickListener {
            startActivity(Intent(this, SdkManagerActivity::class.java))
        }
    }
}
