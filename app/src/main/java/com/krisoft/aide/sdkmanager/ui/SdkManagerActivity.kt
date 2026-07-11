package com.krisoft.aide.sdkmanager.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.krisoft.aide.databinding.ActivitySdkManagerBinding
import kotlinx.coroutines.launch

class SdkManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySdkManagerBinding
    private val viewModel: SdkManagerViewModel by viewModels()

    private val adapter = SdkComponentAdapter(
        onInstall = { component -> viewModel.install(component) },
        onUninstall = { component -> viewModel.uninstall(component) },
        onRetry = { component -> viewModel.retry(component) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySdkManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "SDK Manager"

        binding.recyclerSdkComponents.layoutManager = LinearLayoutManager(this)
        binding.recyclerSdkComponents.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { states -> adapter.submitList(states) }
                }
                launch {
                    viewModel.loadError.collect { message ->
                        if (message != null) {
                            binding.textLoadError.visibility = View.VISIBLE
                            binding.textLoadError.text = message
                        } else {
                            binding.textLoadError.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
