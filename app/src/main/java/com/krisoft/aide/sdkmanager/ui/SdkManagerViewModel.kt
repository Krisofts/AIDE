package com.krisoft.aide.sdkmanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.krisoft.aide.sdkmanager.InstallProgress
import com.krisoft.aide.sdkmanager.InstalledSdkRegistry
import com.krisoft.aide.sdkmanager.SdkComponent
import com.krisoft.aide.sdkmanager.SdkComponentRepository
import com.krisoft.aide.sdkmanager.SdkInstaller
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SdkComponentUiState(
    val component: SdkComponent,
    val installed: Boolean,
    val progress: InstallProgress = InstallProgress.Idle,
)

class SdkManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val sdkRootDir = File(application.filesDir, "sdk")
    private val registry = InstalledSdkRegistry(File(sdkRootDir, "installed.json"))
    private val repository = SdkComponentRepository(application)
    private val installer = SdkInstaller(sdkRootDir, registry)

    private val _uiState = MutableStateFlow<List<SdkComponentUiState>>(emptyList())
    val uiState: StateFlow<List<SdkComponentUiState>> = _uiState.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    init {
        loadManifest()
    }

    /**
     * Belum ada endpoint manifest produksi (lihat SdkComponentRepository), jadi
     * untuk sekarang selalu memuat manifest contoh yang dibundel di assets.
     */
    fun loadManifest() {
        viewModelScope.launch {
            _loadError.value = null
            repository.loadBundledSampleManifest()
                .onSuccess { manifest ->
                    _uiState.value = manifest.components.map { component ->
                        SdkComponentUiState(component = component, installed = installer.isInstalled(component))
                    }
                }
                .onFailure { e ->
                    _loadError.value = e.message ?: "Gagal memuat daftar komponen SDK"
                }
        }
    }

    fun install(component: SdkComponent) {
        viewModelScope.launch {
            installer.install(component) { progress -> updateProgress(component.id, progress) }
            updateInstalledFlag(component)
        }
    }

    fun uninstall(component: SdkComponent) {
        installer.uninstall(component)
        updateInstalledFlag(component)
    }

    /** Dipanggil dari tombol "Coba lagi" - retry murni memanggil ulang [install]. */
    fun retry(component: SdkComponent) = install(component)

    private fun updateProgress(id: String, progress: InstallProgress) {
        _uiState.update { list -> list.map { if (it.component.id == id) it.copy(progress = progress) else it } }
    }

    private fun updateInstalledFlag(component: SdkComponent) {
        _uiState.update { list ->
            list.map {
                if (it.component.id == component.id) it.copy(installed = installer.isInstalled(component)) else it
            }
        }
    }
}
