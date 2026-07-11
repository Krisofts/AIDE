package com.krisoft.aide.sdkmanager

sealed class InstallProgress {
    data object Idle : InstallProgress()
    data class Downloading(val bytesDownloaded: Long, val totalBytes: Long) : InstallProgress()
    data object Verifying : InstallProgress()
    data object Extracting : InstallProgress()
    data object Installed : InstallProgress()

    /**
     * Kegagalan selalu membawa [message] yang jelas & bisa ditindaklanjuti user,
     * dan tidak pernah jadi dead-end - UI selalu menyediakan tombol coba lagi.
     * (Lihat lesson-learned dari percobaan setup ACSIDE di docs/MASTER_PLAN.md §7.)
     */
    data class Failed(val message: String, val cause: Throwable? = null) : InstallProgress()
}
